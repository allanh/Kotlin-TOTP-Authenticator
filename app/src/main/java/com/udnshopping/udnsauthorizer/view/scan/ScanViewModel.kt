package com.udnshopping.udnsauthorizer.view.scan

import android.view.SurfaceHolder
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import com.udnshopping.udnsauthorizer.utility.ULog

class ScanViewModel(private val secretRepository: SecretRepository) : ViewModel() {
    private var surfaceHolder: SurfaceHolder? = null
    /**
     * Create and start the camera.
     */
    fun startCamera(surfaceHolder: SurfaceHolder) {
        ULog.d(TAG, "startCamera")
        this.surfaceHolder = surfaceHolder
//        surfaceHolder.addCallback(surfaceHolderCallback)
    }

    fun addData(text: String?) {
        text ?: return
        secretRepository.addData(text)
    }

    companion object {
        private const val TAG = "GvScanViewModel"
    }
}