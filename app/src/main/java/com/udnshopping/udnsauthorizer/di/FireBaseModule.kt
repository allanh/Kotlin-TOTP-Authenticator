package com.udnshopping.udnsauthorizer.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
        return FirebaseRemoteConfig.getInstance()
    }
}