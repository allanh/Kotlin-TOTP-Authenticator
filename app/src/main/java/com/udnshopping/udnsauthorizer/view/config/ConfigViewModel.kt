package com.udnshopping.udnsauthorizer.view.config

import androidx.lifecycle.MutableLiveData
import com.udnshopping.udnsauthorizer.BuildConfig

class ConfigViewModel {

    private var config = MutableLiveData<Map<String, String>>().apply {
        postValue(mapOf("version" to BuildConfig.VERSION_NAME))
    }

    fun getConfigObservable() = config
}