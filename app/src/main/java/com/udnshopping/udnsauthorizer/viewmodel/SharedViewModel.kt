package com.udnshopping.udnsauthorizer.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import com.udnshopping.udnsauthorizer.MainActivity
import com.udnshopping.udnsauthorizer.data.Pin
import com.udnshopping.udnsauthorizer.data.Secret
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.utilities.ThreeDESUtil
import kotlinx.android.synthetic.main.activity_pins.*
import org.apache.commons.codec.binary.Base32
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SharedViewModel(var activity: Activity?) : ViewModel() {

    private var secrets: MutableList<Secret> = mutableListOf()
    var pins = MutableLiveData<MutableList<Pin>>()
    //private val pinMap: MutableMap<String, Pin> = mutableMapOf()

    init {
        secrets = getSecretList()
        updatePins()
        Logger.d(TAG, "secrets size: ${secrets.size}")
    }

    private fun getSecretList(): MutableList<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = preferences?.getString(kSecretList, "") ?: ""
        return if (json.isNotBlank()) Gson().fromJson(json, type) else mutableListOf()
    }

    fun getPinList() = pins.value

    @Synchronized fun removeAt(position: Int) {
        Logger.d(TAG, "removeAt: $position")
        if (position >= 0 && position < secrets.size) {
            secrets.removeAt(position)
            Logger.d(TAG, "removed secret list size: ${secrets.size}")
            var tempPins = pins.value?.toMutableList()
            tempPins?.removeAt(position)
            pins.value = tempPins
            Logger.d(TAG, "removed pin list size: ${pins.value?.size}")
        }
    }

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

                    if (!(secretInfo == null || secretInfo.isEmpty())) {
                        val time = secretInfo[kTime]
                        val timeFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                        val date = timeFormat.parse(time)
                        val dateString = DATE_FORMAT.format(date)

                        secret = Secret(secretInfo[kSecret], secretInfo[kAccount], dateString)
                    }
                } else {
//                    errorQRCodeAlert()
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                errorQRCodeAlert()
            }
        } else if (!Uri.parse(auth).getQueryParameter(kSecret).isNullOrEmpty()) {
            val uri = Uri.parse(auth)
            val secretKey = uri.getQueryParameter(kSecret)
            val user = uri.path
            if (!secretKey.isNullOrEmpty() && !user.isNullOrEmpty()) {
                secret = Secret(secretKey, user, "")
            }
        } else {
//            errorQRCodeAlert()
        }
        if (secret != null) {
            Logger.d(TAG, "add secret")
            secrets.add(secret)

            //--SAVE Data
            saveData()

            updatePins()
//            my_recycler_view.adapter?.notifyItemInserted(secrets.size)
        }
    }


    private fun updatePins() {
        var pinList = mutableListOf<Pin>()

        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30, timeStepUnit = TimeUnit.SECONDS
        )
        for (secret in secrets) {
            if (secret.key.isNotEmpty() && secret.value.isNotEmpty()) {
                val timeBasedOneTimePasswordGenerator =
                    TimeBasedOneTimePasswordGenerator(Base32().decode(secret.key), config)
                val pinString = timeBasedOneTimePasswordGenerator.generate()
                val progress = SimpleDateFormat("ss").format(Calendar.getInstance().time).toInt()
                val pin = Pin(pinString, secret.value, progress, secret.date)
                pinList.add(pin)
            }
        }

        updatePinListState(pinList)
    }

    private fun updatePinListState(pinList: MutableList<Pin>) {

        var pinMap = mutableMapOf<String, Pin>()
        var tempPins = pinList.toMutableList()

        for (i in tempPins.size-1 downTo 0) {
            var key = tempPins[i].value
            if (!pinMap.containsKey(key)) {
                pinMap[key] = tempPins[i]
            } else {
                val lastPin = pinMap[key] as Pin
                val lastDate = DATE_FORMAT.parse(lastPin.date)
                val pinDate = DATE_FORMAT.parse(tempPins[i].date)
                Logger.d(TAG, "LastDate: ${lastDate.time} PinDate: ${pinDate.time}")
                if (lastDate.time > pinDate.time) {
                    tempPins[i].isValid = false
                }
            }
        }

        pins.value = tempPins
    }

    fun isDataEmpty() = secrets.size == 0

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
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }
}