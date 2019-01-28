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
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.model.Secret
import com.udnshopping.udnsauthorizer.extensions.SingleLiveEvent
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.utilities.ThreeDESUtil
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
        updatePins()
        Logger.d(TAG, "secrets size: ${secrets.size}")
    }

    /**
     * Get the secret list from shared preferences.
     */
    private fun getSecretList(): MutableList<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = preferences?.getString(kSecretList, "") ?: ""
        return if (json.isNotBlank()) Gson().fromJson(json, type) else mutableListOf()
    }

    fun getPinList() = pins.value

    /**
     * Removes an element at the specified [position] from the secret and pin list.
     */
    @Synchronized fun removeAt(position: Int) {
        Logger.d(TAG, "removeAt: $position")
        if (position >= 0 && position < secrets.size) {
            secrets.removeAt(position)
            Logger.d(TAG, "removed secret list size: ${secrets.size}")

            var tempPins = pins.value?.toMutableList()
            tempPins?.removeAt(position)
            isDataEmpty.set(tempPins?.isEmpty() ?: true)
            pins.value = tempPins
            Logger.d(TAG, "removed pin list size: ${pins.value?.size}")
        }
    }

    /**
     * Add an [extra] data to secrets.
     */
    fun addData(extra: Bundle?) {
        val auth = extra?.getString(kAuth)
        var secret: Secret? = null
        val auth_length = auth?.length ?: 0

        Logger.d(TAG, "add Data: $auth")
        if (auth_length > 0 && (auth?.startsWith("otpauth://totp")) == false) {
            val decryptString = auth.substring(2)
            try {
                val decrypt = ThreeDESUtil.decrypt(decryptString)
                if (!decrypt.isNullOrEmpty() && decrypt.contains("acc", ignoreCase = false)) {
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    val json = decrypt
                    val secretInfo = Gson().fromJson<Map<String, String>>(json, type)
                    Logger.d(TAG, "secretInfo: ${secretInfo?.toString()}")

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
            Logger.d(TAG, "add secret")
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
            if (secret.key.isNotEmpty() && secret.value.isNotEmpty() && secret.date.isNotEmpty()) {
                val timeBasedOneTimePasswordGenerator =
                    TimeBasedOneTimePasswordGenerator(Base32().decode(secret.key), config)
                val pinString = timeBasedOneTimePasswordGenerator.generate()
                val progress = SimpleDateFormat("ss").format(Calendar.getInstance().time).toInt()
                val pin = Pin(pinString, secret.value, progress, secret.date)
                pinList.add(pin)

                // Update the last time map
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
        updatePinListState(lastTimeMap, pinList)
    }

    /**
     * Update the pins state with the [lastTimeMap].
     */
    private fun updatePinListState(lastTimeMap: MutableMap<String, Long>, pinList: MutableList<Pin>) {
        val tempPins = pinList.toMutableList()
        for (pin in tempPins) {
            val pinTime = PIN_DATE_FORMAT.parse(pin.date).time
            pin.isValid = (pinTime == lastTimeMap[pin.value])
        }
        isDataEmpty.set(tempPins.isEmpty())
        pins.value = tempPins
    }

    /**
     * Save the secrets to shared preferences.
     */
    fun saveData() {
        Logger.d(TAG, "save data: ${secrets.size}")
        //--SAVE Data
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = preferences?.edit()
        val type = object : TypeToken<List<Secret>>() {}.type
        val json = Gson().toJson(secrets, type)
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