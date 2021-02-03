package com.udnshopping.udnsauthorizer.view.sendcode

import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.QRCodeRepository

class SendCodeViewModel(private val repository: QRCodeRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        repository.cancel()
    }

    fun sendEmail(email: String)  = repository.sendEmail(email)

    fun getResultObservable() = repository.result

}