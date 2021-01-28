package com.udnshopping.udnsauthorizer.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.udnshopping.udnsauthorizer.BuildConfig
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.BroadcastMessage
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
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default)
    }

    /**
     * Fetch a email input config from the Remote Config service.
     */
    fun fetchRemoteConfig() {

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ULog.d(TAG, "Fetch successful")

                    val forceUpdateVersion = remoteConfig.getString(FORCE_UPDATE_VERSION_KEY)
                    val isEmailInput = remoteConfig.getBoolean(EMAIL_INPUT_KEY)
                    val isForceUpdate = remoteConfig.getBoolean(FORCE_UPDATE_KEY)
                    val broadcast = remoteConfig.getBoolean(BROADCAST_KEY)
                    val broadcastMessageString = remoteConfig.getString(BROADCAST_MESSAGE_KEY)
                    val broadcastMessage = Gson().fromJson(broadcastMessageString, BroadcastMessage::class.java)

                    ULog.d(TAG, "forceUpdateVersion: $forceUpdateVersion, isEmailInput: $isEmailInput, isForceUpdate: $isForceUpdate")
                    ULog.d(TAG, "broadcast: $broadcast, broadcastMessage: $broadcastMessage")
                    val config = UdnRemoteConfig(isEmailInput, isForceUpdate, forceUpdateVersion, broadcast, broadcastMessage)
                    udnRemoteConfig.postValue(config)
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
        private const val BROADCAST_KEY = "broadcast"
        private const val BROADCAST_MESSAGE_KEY = "broadcast_message"
    }
}