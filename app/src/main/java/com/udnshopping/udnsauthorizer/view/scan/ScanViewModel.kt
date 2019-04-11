package com.udnshopping.udnsauthorizer.view.scan

import android.view.SurfaceHolder
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.utility.ULog
import javax.inject.Inject

class ScanViewModel @Inject
constructor(private var surfaceHolderCallback: SurfaceHolderCallback) : ViewModel() {
    private var surfaceHolder: SurfaceHolder? = null
    /**
     * Create and start the camera.
     */
    fun startCamera(surfaceHolder: SurfaceHolder) {
        ULog.d(TAG, "startCamera")
        this.surfaceHolder = surfaceHolder
        surfaceHolder.addCallback(surfaceHolderCallback)
    }

    companion object {
        private const val TAG = "ScanViewModel"
    }
}