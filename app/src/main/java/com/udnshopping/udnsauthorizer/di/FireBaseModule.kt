package com.udnshopping.udnsauthorizer.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.repository.RemoteConfigRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FireBaseModule {
    @Provides
    @Singleton
    fun provideRemoteConfigRepository(remoteConfig: FirebaseRemoteConfig): RemoteConfigRepository {
        return RemoteConfigRepository(remoteConfig)
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        // Get Remote Config instance.
        val remoteConfig = Firebase.remoteConfig

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. Also use Remote Config
        // Setting to set the minimum fetch interval.
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        return remoteConfig
    }
}