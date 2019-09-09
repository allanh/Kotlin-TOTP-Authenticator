package com.udnshopping.udnsauthorizer.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import com.udnshopping.udnsauthorizer.model.SingleLiveEvent
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.model.Secret
import com.udnshopping.udnsauthorizer.utility.LocaleUtil
import com.udnshopping.udnsauthorizer.utility.ThreeDESUtil
import com.udnshopping.udnsauthorizer.utility.ULog
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecretRepository @Inject
constructor(context: Context, private val preferences: SharedPreferences) {

    /**
     * This is the job for all coroutines started by this repository.
     *
     * Cancelling this job will cancel all coroutines started by this repository.
     */
    private val repositoryJob = Job()

    /**
     * This is the io scope for all coroutines launched by SecretRepository.
     *
     * Since we pass ioJob, you can cancel all coroutines launched by ioScope by calling
     * ioJob.cancel()
     */
    private val ioScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val originDataFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", LocaleUtil.getCurrent(context))
    private val pinDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", LocaleUtil.getCurrent(context))
    
    private var secrets: MutableList<Secret> = mutableListOf()
    private val showQRCodeErrorEvent = SingleLiveEvent<Any>()
    private var pins = MutableLiveData<MutableList<Pin>>()

    init {
        ULog.d(TAG, "Injection SecretRepository")
        initData()
    }

    /**
     * Init data.
     */
    private fun initData() = ioScope.launch {
        try {
            ULog.d(TAG, "loadData")
            secrets = withContext(Dispatchers.IO) {
                loadSecretList()
            }
            ULog.d(TAG, "secrets size: ${secrets.size}")
            updatePins()
            ULog.d(TAG, "loadData done: ${secrets.size}")
        } catch (e: Exception) {
            ULog.e(TAG, "errorMessage: ${e.message}")
        }
    }

    /**
     * Get the secret list from SharedPreferences.
     */
    private fun loadSecretList(): MutableList<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        ULog.d(TAG, "preferences: $preferences")
        val json = preferences.getString(KEY_SECRET_LIST, "") ?: ""
        ULog.d(TAG, "json: $json")
        return if (json.isNotBlank()) Gson().fromJson(json, type) else mutableListOf()
    }

    /**
     * Update the pins state with the [lastTimeMap].
     */
    private fun updatePinListState(lastTimeMap: MutableMap<String, Long>, pinList: MutableList<Pin>) {
        val tempPins = pinList.toMutableList()
        for (pin in tempPins) {
            if (pin.date.isNotEmpty()) {
                val pinTime = pinDateFormat.parse(pin.date).time
                pin.isValid = (pinTime == lastTimeMap[pin.value])
            } else {
                pin.isValid = true
            }
        }
        pins.postValue(tempPins)
    }

    /**
     * Add an [auth] data to secrets.
     */
    fun addData(auth: String) {
        var secret: Secret? = null
        val authLength = auth.length

        ULog.d(TAG, "add Data: $auth")
        if (authLength > 2 && !auth.startsWith(AUTH_PREFIX)) {
            val decryptString = auth.substring(2)
            try {
                val json = ThreeDESUtil.decrypt(decryptString)
                if (json.isNotEmpty() && json.contains("acc", ignoreCase = false)) {
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    val secretInfo = Gson().fromJson<Map<String, String>>(json, type)
                    ULog.d(TAG, "secretInfo: ${secretInfo?.toString()}")

                    // Change the data format
                    if (!(secretInfo == null || secretInfo.isEmpty())) {
                        val time = secretInfo[KEY_TIME]
                        val date = originDataFormat.parse(time)
                        val dateString = pinDateFormat.format(date)
                        secret = Secret(Date().time, secretInfo[KEY_SECRET], secretInfo[KEY_ACCOUNT], dateString)
                    }
                } else {
                    showQRCodeErrorEvent.call()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showQRCodeErrorEvent.call()
            }
        } else if (!Uri.parse(auth).getQueryParameter(KEY_SECRET).isNullOrEmpty()) {
            val uri = Uri.parse(auth)
            val secretKey = uri.getQueryParameter(KEY_SECRET)
            val user = uri.path
            if (!secretKey.isNullOrEmpty() && !user.isNullOrEmpty()) {
                secret = Secret(Date().time, secretKey, user, "")
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
            timeStep = UPDATE_TIME, timeStepUnit = TimeUnit.SECONDS
        )
        for (secret in secrets) {
            if (secret.key.isNotEmpty() && secret.value.isNotEmpty()) {
                val key = secret.key.toByteArray()
                val timeBasedOneTimePasswordGenerator =
                    TimeBasedOneTimePasswordGenerator(key, config)
                val pinString = timeBasedOneTimePasswordGenerator.generate()
                val diffTime = Date().time - secret.startTime
                val progress = TimeUnit.MILLISECONDS.toSeconds(diffTime) % UPDATE_TIME
                val pin = Pin(pinString, secret.value, progress.toInt(), secret.date)
                pinList.add(pin)

                // Update the last time map
                if (secret.date.isNotEmpty()) {
                    val pinTimeStamp = pinDateFormat.parse(secret.date).time
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
     * Removes an element at the specified [position] from the secret and pin list.
     */
    @Synchronized fun removeAt(position: Int) {
        ULog.d(TAG, "removeAt: $position")
        if (position >= 0 && position < secrets.size) {
            secrets.removeAt(position)
            ULog.d(TAG, "removed secret list size: ${secrets.size}")

            val tempPins = pins.value?.toMutableList()
            tempPins?.removeAt(position)
            pins.value = tempPins
            ULog.d(TAG, "removed pin list size: ${pins.value?.size}")
        }
    }

    /**
     * Save the secrets to shared preferences.
     */
    fun saveData() {
        ULog.d(TAG, "save data: ${secrets.size}")
        //--SAVE Data
        val editor = preferences.edit()
        val type = object : TypeToken<List<Secret>>() {}.type
        val json = Gson().toJson(secrets.toList(), type)
        editor?.putString(KEY_SECRET_LIST, json)?.apply()
        editor?.commit()
    }

    fun getPinsObservable() = pins

    fun getQRCodeErrorEventObservable() = showQRCodeErrorEvent

    companion object {
        private const val TAG = "SecretRepository"
        private const val KEY_SECRET = "secret"
        private const val KEY_SECRET_LIST = "secretList"
        private const val KEY_ACCOUNT = "acc"
        private const val KEY_TIME = "time"
        const val UPDATE_TIME = 120L
        const val AUTH_PREFIX = "otpauth://totp"
    }
}