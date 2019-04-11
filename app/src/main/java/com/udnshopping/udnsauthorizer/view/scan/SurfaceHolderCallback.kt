package com.udnshopping.udnsauthorizer.view.scan

import android.annotation.SuppressLint
import android.view.SurfaceHolder
import com.google.android.gms.vision.CameraSource
import com.udnshopping.udnsauthorizer.utility.ULog
import java.io.IOException
import javax.inject.Inject

class SurfaceHolderCallback @Inject constructor(private val cameraSource: CameraSource) : SurfaceHolder.Callback {
    @SuppressLint("MissingPermission")
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            cameraSource.start(holder)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        ULog.d(TAG, "surfaceDestroyed")
        cameraSource.stop()
    }

    companion object {
        private const val TAG = "SurfaceHolderCallback"
    }
}