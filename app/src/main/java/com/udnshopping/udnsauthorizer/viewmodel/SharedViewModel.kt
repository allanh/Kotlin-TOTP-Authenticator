package com.udnshopping.udnsauthorizer.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.model.Secret
import com.udnshopping.udnsauthorizer.extension.SingleLiveEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.utility.ThreeDESUtil
import org.apache.commons.codec.binary.Base32
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SharedViewModel(var activity: Activity?) : ViewModel() {

    private var secrets: MutableList<Secret> = mutableListOf()
    val showQRCodeErrorEvent = SingleLiveEvent<Any>()
    var pins = MutableLiveData<MutableList<Pin>>()
    var isDataEmpty = ObservableBoolean(false)

    init {
        secrets = getSecretList()
        ULog.d(TAG, "secrets size: ${secrets.size}")
        isDataEmpty.set(secrets.isEmpty())
        updatePins()
    }

    /**
     * Get the secret list from shared preferences.
     */
    private fun getSecretList(): MutableList<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        val preferences = activity?.getSharedPreferences(
            activity?.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        ULog.d(TAG, "preferences: ${preferences.toString()}")
        val json = preferences?.getString(kSecretList, "") ?: ""
        ULog.d(TAG, "json: $json")
        return if (json.isNotBlank()) Gson().fromJson(json, type) else mutableListOf()
    }

    fun getPinList() = pins.value

    /**
     * Removes an element at the specified [position] from the secret and pin list.
     */
    @Synchronized fun removeAt(position: Int) {
        ULog.d(TAG, "removeAt: $position")
        if (position >= 0 && position < secrets.size) {
            secrets.removeAt(position)
            ULog.d(TAG, "removed secret list size: ${secrets.size}")

            var tempPins = pins.value?.toMutableList()
            tempPins?.removeAt(position)
            isDataEmpty.set(tempPins?.isEmpty() ?: true)
            pins.value = tempPins
            ULog.d(TAG, "removed pin list size: ${pins.value?.size}")
        }
    }

    /**
     * Add an [extra] data to secrets.
     */
    fun addData(extra: Bundle?) {
        val auth = extra?.getString(kAuth)
        var secret: Secret? = null
        val auth_length = auth?.length ?: 0

        ULog.d(TAG, "add Data: $auth")
        if (auth_length > 0 && (auth?.startsWith("otpauth://totp")) == false) {
            val decryptString = auth.substring(2)
            try {
                val decrypt = ThreeDESUtil.decrypt(decryptString)
                if (!decrypt.isNullOrEmpty() && decrypt.contains("acc", ignoreCase = false)) {
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    val json = decrypt
                    val secretInfo = Gson().fromJson<Map<String, String>>(json, type)
                    ULog.d(TAG, "secretInfo: ${secretInfo?.toString()}")

                    // Change the data format
                    if (!(secretInfo == null || secretInfo.isEmpty())) {
                        val time = secretInfo[kTime]
                        val date = ORIGIN_DATE_FORMAT.parse(time)
                        val dateString = PIN_DATE_FORMAT.format(date)
                        secret = Secret(secretInfo[kSecret], secretInfo[kAccount], dateString)
                    }
                } else {
                    showQRCodeErrorEvent.call()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showQRCodeErrorEvent.call()
            }
        } else if (!Uri.parse(auth).getQueryParameter(kSecret).isNullOrEmpty()) {
            val uri = Uri.parse(auth)
            val secretKey = uri.getQueryParameter(kSecret)
            val user = uri.path
            if (!secretKey.isNullOrEmpty() && !user.isNullOrEmpty()) {
                secret = Secret(secretKey, user, "")
            }
        } else {
            showQRCodeErrorEvent.call()
        }
        if (secret != null) {
            ULog.d(TAG, "add secret: ${secret.key}")
            secrets.add(secret)

            //--SAVE Data
            saveData()
            updatePins()
        }
    }

    /**
     * Update the pin list.
     */
    fun updatePins() {
        val pinList = mutableListOf<Pin>()
        val lastTimeMap = mutableMapOf<String, Long>()

        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 60, timeStepUnit = TimeUnit.SECONDS
        )
        for (secret in secrets) {
            if (secret.key.isNotEmpty() && secret.value.isNotEmpty()) {
                val key = Base32().decode(secret.key)
                val timeBasedOneTimePasswordGenerator =
                    TimeBasedOneTimePasswordGenerator(key, config)
                val pinString = timeBasedOneTimePasswordGenerator.generate()
                val progress = SimpleDateFormat("ss").format(Calendar.getInstance().time).toInt()
                val pin = Pin(pinString, secret.value, progress, secret.date)
                pinList.add(pin)

                // Update the last time map
                if (secret.date.isNotEmpty()) {
                    val pinTimeStamp = PIN_DATE_FORMAT.parse(secret.date).time
                    if (lastTimeMap.containsKey(secret.value)) {
                        lastTimeMap[secret.value]?.let {
                            if (it < pinTimeStamp)
                                lastTimeMap[secret.value] = pinTimeStamp
                        }
                    } else {
                        lastTimeMap[secret.value] = pinTimeStamp
                    }
                }
            }
        }
        updatePinListState(lastTimeMap, pinList)
    }

    /**
     * Update the pins state with the [lastTimeMap].
     */
    private fun updatePinListState(lastTimeMap: MutableMap<String, Long>, pinList: MutableList<Pin>) {
        val tempPins = pinList.toMutableList()
        for (pin in tempPins) {
            if (pin.date.isNotEmpty()) {
                val pinTime = PIN_DATE_FORMAT.parse(pin.date).time
                pin.isValid = (pinTime == lastTimeMap[pin.value])
            } else {
                pin.isValid = true
            }
        }
        isDataEmpty.set(tempPins.isEmpty())
        pins.value = tempPins
    }

    /**
     * Save the secrets to shared preferences.
     */
    fun saveData() {
        ULog.d(TAG, "save data: ${secrets.size}")
        //--SAVE Data
        val preferences = activity?.getSharedPreferences(
            activity?.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val editor = preferences?.edit()
        val type = object : TypeToken<List<Secret>>() {}.type
        val json = Gson().toJson(secrets.toList(), type)
        editor?.putString(kSecretList, json)?.apply()
        editor?.commit()
    }

    companion object {

        private const val TAG = "SharedViewModel"

        private val kAuth = "auth"
        private val kSecret = "secret"
        private val kSecretList = "secretList"
        private val kAccount = "acc"
        private val kTime = "time"

        private val ORIGIN_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        private val PIN_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }
}