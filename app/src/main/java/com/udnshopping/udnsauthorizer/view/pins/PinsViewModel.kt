package com.udnshopping.udnsauthorizer.view.pins

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.repository.SecretRepository

class PinsViewModel
constructor(private val secretRepository: SecretRepository) : ViewModel() {

    private var pins = secretRepository.getPinsObservable()
    private var handler = Handler(Looper.getMainLooper())
    private var isPosted = false
//    private var isRefreshing = true
//        set(value) {
//            synchronized(value) {
//                if (value) {
//                    handler.postDelayed(updateThread, UPDATE_TIME)
//                    isPosted = true
//                } else if (isPosted) {
//                    handler.removeCallbacks(updateThread)
//                    isPosted = false
//                }
//            }
//        }

//    private val updateThread = object: Runnable {
//        override fun run() {
//            updatePins()
//            handler.postDelayed(this, UPDATE_TIME)
//        }
//    }
//
//    var isDataEmptyObservable: LiveData<Boolean> = Transformations.map(pins) { pinList ->
//        if (pinList.isEmpty() && isPosted) {
//            isRefreshing = false
//        } else if(!isPosted) {
//            isRefreshing = true
//        }
//        pinList.isEmpty()
//    }

    /**
     * Removes an element at the specified [position] from the secret and pin list.
     */
    fun removeAt(position: Int) = secretRepository.removeAt(position)

    /**
     * Update the pin list.
     */
    fun updatePins() = secretRepository.updatePins()

    fun getPinsObservable(): MutableLiveData<MutableList<Pin>> = pins

    fun enableRefresh(enable: Boolean) {
//        isRefreshing = enable
    }

    companion object {
        private const val TAG = "PinsViewModel"
        private const val UPDATE_TIME = 1000L
    }
}