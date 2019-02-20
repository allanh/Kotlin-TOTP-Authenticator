package com.udnshopping.udnsauthorizer.view.pins

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import com.udnshopping.udnsauthorizer.utility.ULog
import javax.inject.Inject

class PinsViewModel @Inject
constructor(private val secretRepository: SecretRepository) : ViewModel() {

    private var pins = secretRepository.getPinsObservable()

    init {
        ULog.d(TAG, "init")
    }

    /**
     * Removes an element at the specified [position] from the secret and pin list.
     */
    fun removeAt(position: Int) = secretRepository.removeAt(position)

    /**
     * Update the pin list.
     */
    fun updatePins() = secretRepository.updatePins()

    fun isDataEmptyObservable(): LiveData<Boolean> = Transformations.map(pins) {
            pinList -> pinList.isEmpty()
    }

    fun getPinsObservable(): MutableLiveData<MutableList<Pin>> = pins

    companion object {
        private const val TAG = "PinsViewModel"
    }
}