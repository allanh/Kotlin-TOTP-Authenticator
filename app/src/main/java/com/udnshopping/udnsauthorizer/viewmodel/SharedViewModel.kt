package com.udnshopping.udnsauthorizer.viewmodel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SharedViewModel(var activity: Activity?) : ViewModel() {

    private val secrets: MutableList<Secret> = mutableListOf()
    private val pins: MutableList<Pin> = mutableListOf()
    private val pinMap: MutableMap<String, Pin> = mutableMapOf()

    init {
        Logger.d(TAG, "init")
        secrets += getSecretList()
        Logger.d(TAG, "secrets size: ${secrets.size}")
    }

    fun getSecretList(): List<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = preferences?.getString(kSecretList, "") ?: ""
        return if (json.isNotBlank()) Gson().fromJson(json, type) else listOf()
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
            addSecret(secret)

            //--SAVE Data
            saveData()

            updatePinsAfterClear()
//            my_recycler_view.adapter?.notifyItemInserted(secrets.size)
        }
    }


    private fun updatePinsAfterClear() {
        pins.clear()
        pinMap.clear()
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
                addPin(pin)
            }
        }

        updatePinListState()
    }

    private fun updatePinListState() {
        for (i in pins.size-1 downTo 0) {
            var key = pins[i].value
            if (!pinMap.containsKey(key)) {
                pinMap[key] = pins[i]
            } else {
                val lastPin = pinMap[key] as Pin
                val lastDate = DATE_FORMAT.parse(lastPin.date)
                val pinDate = DATE_FORMAT.parse(pins[i].date)
                Logger.d(TAG, "LastDate: ${lastDate.time} PinDate: ${pinDate.time}")
                if (lastDate.time > pinDate.time) {
                    pins[i].isValid = false
                }
            }
        }
    }

    private fun addPin(pin: Pin) {
        Logger.d(TAG, "addPin: ${pin.value}")
        pins += pin
    }

    private fun addSecret(secret: Secret) {
        Logger.d(TAG, "addSecret: ${secret.value}")
        secrets += secret
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