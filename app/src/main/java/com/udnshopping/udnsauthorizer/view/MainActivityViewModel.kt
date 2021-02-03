package com.udnshopping.udnsauthorizer.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.RemoteConfigRepository
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import com.udnshopping.udnsauthorizer.utility.ULog

class MainActivityViewModel constructor(
    private val configRepository: RemoteConfigRepository,
    private val secretRepository: SecretRepository
) : ViewModel() {

    private var udnRemoteConfig = configRepository.getRemoteConfigObservable()
    private var pins = secretRepository.getPinsObservable()
    private var forceUpdateVersion = "1.0.0"
    private var isNeedForceUpdate = false
    private var _isShowBroadcast = false

    var isEmailInputObservable = MutableLiveData<Boolean>()

    var isDataNotEmptyObservable: LiveData<Boolean> = Transformations.map(pins) { pinList ->
        pinList.isNotEmpty()
    }

    var isForceUpdateObservable: LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
        forceUpdateVersion = config.forceUpdateVersion ?: "1.0.0"
        _isShowBroadcast = config.broadcast ?: false
        isEmailInputObservable.postValue(config.isEmailInput)
        config.isForceUpdate
    }

    init {
        ULog.d(TAG, "init")
    }

    /**
     * Fetch the remote config from FireBase cloud.
     */
    fun fetchRemoteConfig() = configRepository.fetchRemoteConfig()

    fun getQRCodeErrorEventObservable() = secretRepository.getQRCodeErrorEventObservable()

    fun addData(auth: String?) = secretRepository.addData(auth)

    /**
     * Save the secrets to shared preferences.
     */
    fun saveData() = secretRepository.saveData()

    fun checkApkVersion(currentVersion: String): Boolean {
        ULog.d(TAG, "check: $currentVersion, force: $forceUpdateVersion")
        val updateVersion = forceUpdateVersion
        isNeedForceUpdate = currentVersion < updateVersion
        return isNeedForceUpdate
    }

    fun isShowBroadcast(): Boolean = !isNeedForceUpdate && _isShowBroadcast

    fun getBroadcastTitle() = udnRemoteConfig.value?.broadcastMessage?.title ?: ""

    fun getBroadcastBody() = udnRemoteConfig.value?.broadcastMessage?.body ?: ""

    fun onEmailClick() {

    }

    fun onScanClick() {

    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}