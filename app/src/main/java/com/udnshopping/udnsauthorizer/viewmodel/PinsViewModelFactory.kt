package com.udnshopping.udnsauthorizer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udnshopping.udnsauthorizer.repository.SecretRepository

class PinsViewModelFactory(val secretRepository: SecretRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PinsViewModel(secretRepository) as T
    }
}