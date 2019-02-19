package com.udnshopping.udnsauthorizer.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.udnshopping.udnsauthorizer.BuildConfig
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.UdnRemoteConfig
import com.udnshopping.udnsauthorizer.utility.ULog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigRepository @Inject
constructor(private val remoteConfig: FirebaseRemoteConfig) {

    private var udnRemoteConfig: MutableLiveData<UdnRemoteConfig> = MutableLiveData()

    init {
        ULog.d(TAG, "Injection RemoteConfigRepository")
        initRemoteConfig()
    }

    /**
     * Get Remote Config instance and Set default Remote Config parameter values.
     */
    private fun initRemoteConfig() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_config_default)
    }

    /**
     * Fetch a email input config from the Remote Config service.
     */
    fun fetchRemoteConfig() {
        val isUsingDeveloperMode = remoteConfig.info.configSettings.isDeveloperModeEnabled

        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        val cacheExpiration: Long = if (isUsingDeveloperMode) {
            0
        } else {
            3600 // 1 hour in seconds.
        }

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        remoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ULog.d(TAG, "Fetch successful")
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    remoteConfig.activateFetched()

                    val forceUpdateVersion = remoteConfig.getString(FORCE_UPDATE_VERSION_KEY)
                    val isEmailInput = remoteConfig.getBoolean(EMAIL_INPUT_KEY)
                    val isForceUpdate = remoteConfig.getBoolean(FORCE_UPDATE_KEY)
                    ULog.d(TAG, "forceUpdateVersion: $forceUpdateVersion, isEmailInput: $isEmailInput, isForceUpdate: $isForceUpdate")
                    udnRemoteConfig.postValue(UdnRemoteConfig(isEmailInput, isForceUpdate, forceUpdateVersion))
                } else {
                    ULog.e(TAG, "Fetch Failed")
                }
            }
    }

    fun getRemoteConfigObservable() = udnRemoteConfig

    companion object {
        const val TAG = "RemoteConfigRepository"

        // Remote Config keys
        private const val EMAIL_INPUT_KEY = "email_input_enabled"

        private const val FORCE_UPDATE_KEY = "force_update"

        private const val FORCE_UPDATE_VERSION_KEY = "Android_force_update_version"
    }
}