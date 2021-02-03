package com.udnshopping.udnsauthorizer

import android.app.Application
import android.content.Context
import com.udnshopping.udnsauthorizer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class UdnApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android context
            androidContext(this@UdnApplication)
            // modules
            modules(listOf(appModule))
        }
    }
}
