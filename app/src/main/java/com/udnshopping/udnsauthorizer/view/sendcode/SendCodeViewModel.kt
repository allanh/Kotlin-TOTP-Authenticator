package com.udnshopping.udnsauthorizer.view.sendcode

import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.QRCodeRepository
import javax.inject.Inject

class SendCodeViewModel @Inject
constructor(private val repository: QRCodeRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        repository.cancel()
    }

    fun sendEmail(email: String)  = repository.sendEmail(email)

    fun getResultObservable() = repository.result

}