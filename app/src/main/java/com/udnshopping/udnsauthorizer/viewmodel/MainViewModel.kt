package com.udnshopping.udnsauthorizer.viewmodel

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.udnshopping.udnsauthorizer.BuildConfig
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.utility.singleArgViewModelFactory


class MainViewModel(var activity: Activity?) : ViewModel() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    var isEmailInput = ObservableBoolean(false)

    init {
        initRemoteConfig()
        fetchRemoteConfig()
    }

    /**
     * Get Remote Config instance and Set default Remote Config parameter values.
     */
    private fun initRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_config_default)
    }

    /**
     * Fetch a email input config from the Remote Config service.
     */
    private fun fetchRemoteConfig() {

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
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    ULog.d(TAG, "Fetch successful")
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    remoteConfig.activateFetched()
                    ULog.d(TAG, "isEmailInput: ${remoteConfig.getBoolean(EMAIL_INPUT_CONFIG_KEY)}")
                    isEmailInput.set(remoteConfig.getBoolean(EMAIL_INPUT_CONFIG_KEY))
                } else {
                    ULog.e(TAG, "Fetch Failed")
                }
            }
    }

    companion object {

        private const val TAG = "MainViewModel"

        // Remote Config keys
        private const val EMAIL_INPUT_CONFIG_KEY = "email_input_enabled"

        val FACTORY = singleArgViewModelFactory(::MainViewModel)

    }
}