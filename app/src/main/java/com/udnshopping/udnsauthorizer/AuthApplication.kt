package com.udnshopping.udnsauthorizer

import com.google.firebase.analytics.FirebaseAnalytics
import com.udnshopping.udnsauthorizer.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

@Suppress("unused")
class AuthApplication : DaggerApplication() {

    private val appComponent = DaggerAppComponent.builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()
        // Initialize FireBase.
        FirebaseAnalytics.getInstance(this)
        appComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }
}