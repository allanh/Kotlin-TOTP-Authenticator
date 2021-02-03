package com.udnshopping.udnsauthorizer.di

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.repository.QRCodeRepository
import com.udnshopping.udnsauthorizer.repository.RemoteConfigRepository
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import com.udnshopping.udnsauthorizer.utility.ThreeDESUtil
import com.udnshopping.udnsauthorizer.view.MainActivityViewModel
import com.udnshopping.udnsauthorizer.view.config.ConfigViewModel
import com.udnshopping.udnsauthorizer.view.pins.PinsViewModel
import com.udnshopping.udnsauthorizer.view.scan.ScanViewModel
import com.udnshopping.udnsauthorizer.view.sendcode.SendCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
val appModule = module {
    single { ThreeDESUtil() }
    single { provideRemoteConfig() }
    single { provideSharePreferences(get()) }

    // Repository
    single { QRCodeRepository(get()) }
    single { provideRemoteConfigRepository(get()) }
    single { provideSecretRepository(get(), get(), get()) }

    // ViewModel
    viewModel { MainActivityViewModel(get(), get()) }
    viewModel { ConfigViewModel() }
    viewModel { PinsViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { SendCodeViewModel(get()) }
}

fun provideRemoteConfigRepository(remoteConfig: FirebaseRemoteConfig): RemoteConfigRepository {
    return RemoteConfigRepository(remoteConfig)
}

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

// Google Vision



// SharePreferences
fun provideSharePreferences(@NonNull context: Context): SharedPreferences {
    return context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
    )
}

fun provideSecretRepository(context: Context, desUtil: ThreeDESUtil, sharedPreferences: SharedPreferences): SecretRepository {
    return SecretRepository(context, desUtil, sharedPreferences)
}