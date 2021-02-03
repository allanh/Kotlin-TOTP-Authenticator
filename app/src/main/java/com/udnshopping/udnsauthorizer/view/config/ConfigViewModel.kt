package com.udnshopping.udnsauthorizer.view.config

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.BuildConfig

class ConfigViewModel : ViewModel() {

    private var config = MutableLiveData<Map<String, String>>().apply {
        postValue(mapOf("version" to BuildConfig.VERSION_NAME))
    }

    fun getConfigObservable() = config
}