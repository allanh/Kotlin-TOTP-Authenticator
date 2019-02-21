package com.udnshopping.udnsauthorizer.view.scan

import android.view.SurfaceHolder
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ScanViewModel @Inject
constructor(private var surfaceHolderCallback: SurfaceHolderCallback) : ViewModel() {

    /**
     * Creates and starts the camera.
     */
    fun startCamera(surfaceHolder: SurfaceHolder) {
        surfaceHolder.addCallback(surfaceHolderCallback)
    }

    companion object {
        private const val TAG = "ScanViewModel"
    }
}