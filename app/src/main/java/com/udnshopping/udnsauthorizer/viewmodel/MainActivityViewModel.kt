package com.udnshopping.udnsauthorizer.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.RemoteConfigRepository
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import com.udnshopping.udnsauthorizer.utility.ULog
import javax.inject.Inject


class MainActivityViewModel  @Inject
constructor(private val configRepository: RemoteConfigRepository, private val secretRepository: SecretRepository) : ViewModel() {

    private var udnRemoteConfig = configRepository.getRemoteConfigObservable()
    private var pins = secretRepository.getPinsObservable()
    private var forceUpdateVersion = "1.0.0"
    var isForceUpdateObservable: LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
        forceUpdateVersion = config.forceUpdateVersion
        config.isForceUpdate
    }

    var isEmailInputObservable: LiveData<Boolean> = Transformations.map(udnRemoteConfig) {
            config -> config.isEmailInput
    }

    init {
        ULog.d(TAG, "init")
    }

    fun fetchRemoteConfig() = configRepository.fetchRemoteConfig()

    fun isDataEmptyObservable(): LiveData<Boolean> = Transformations.map(pins) {
            pinList -> pinList.isEmpty()
    }

    fun getQRCodeErrorEventObservable() = secretRepository.getQRCodeErrorEventObservable()

    fun addData(extra: Bundle?) = secretRepository.addData(extra)

    /**
     * Save the secrets to shared preferences.
     */
    fun saveData() = secretRepository.saveData()

    fun checkApkVersion(currentVersion: String): Boolean {
        ULog.d(TAG, "check: $currentVersion, force: $forceUpdateVersion")
        val updateVersion = forceUpdateVersion
        return currentVersion < updateVersion
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}