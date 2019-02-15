package com.udnshopping.udnsauthorizer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.RemoteConfigRepository
import com.udnshopping.udnsauthorizer.utility.ULog
import javax.inject.Inject


class MainActivityViewModel  @Inject
constructor(private val repository: RemoteConfigRepository) : ViewModel() {

    private var udnRemoteConfig = repository.getRemoteConfigObservable()
    private var forceUpdateVersion = "1.0.0"

//    private val isEmailInput: LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
//        ULog.d(TAG, "new isEmailInput: ${config.isEmailInput}")
//        config.isEmailInput
//    }

    init {
        ULog.d(TAG, "init")
    }

    fun fetchRemoteConfig() {
        repository.fetchRemoteConfig()
    }

    fun isForceUpdateObservable(): LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
        ULog.d(TAG, "new forceUpdateVersion: ${config.forceUpdateVersion}")
        forceUpdateVersion = config.forceUpdateVersion
        config.isForceUpdate
    }

    fun isEmailInputObservable(): LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
        ULog.d(TAG, "new isEmailInput: ${config.isEmailInput}")
        config.isEmailInput
    }

    fun checkApkVersion(currentVersion: String): Boolean {
        ULog.d(TAG, "check: $currentVersion, force: $forceUpdateVersion")
        val updateVersion = forceUpdateVersion
        return currentVersion < updateVersion
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}