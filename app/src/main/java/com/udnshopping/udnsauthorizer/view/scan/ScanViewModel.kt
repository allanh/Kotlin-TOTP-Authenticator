package com.udnshopping.udnsauthorizer.view.scan

import android.annotation.SuppressLint
import android.view.SurfaceHolder
import androidx.lifecycle.ViewModel
import com.google.android.gms.vision.CameraSource
import java.io.IOException

class ScanViewModel : ViewModel() {


//    fun getCallback(cameraSource: CameraSource?): SurfaceHolder.Callback {
//        return object : SurfaceHolder.Callback {
//            @SuppressLint("MissingPermission")
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                try {
//                    cameraSource?.start(holder)
//                } catch (ex: IOException) {
//                    ex.printStackTrace()
//                }
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                cameraSource?.stop()
//            }
//        }
//    }
//    private var udnRemoteConfig = configRepository.getRemoteConfigObservable()
//    private var pins = secretRepository.getPinsObservable()
//    private var forceUpdateVersion = "1.0.0"
//    var isForceUpdateObservable: LiveData<Boolean> = Transformations.map(udnRemoteConfig) { config ->
//        forceUpdateVersion = config.forceUpdateVersion
//        config.isForceUpdate
//    }
//
//    var isEmailInputObservable: LiveData<Boolean> = Transformations.map(udnRemoteConfig) {
//            config -> config.isEmailInput
//    }
//
//    init {
//        ULog.d(TAG, "init")
//    }
//
//    fun fetchRemoteConfig() = configRepository.fetchRemoteConfig()
//
//    fun isDataEmptyObservable(): LiveData<Boolean> = Transformations.map(pins) {
//            pinList -> pinList.isEmpty()
//    }
//
//    fun getQRCodeErrorEventObservable() = secretRepository.getQRCodeErrorEventObservable()
//
//    fun addData(extra: Bundle?) = secretRepository.addData(extra)
//
//    /**
//     * Save the secrets to shared preferences.
//     */
//    fun saveData() = secretRepository.saveData()
//
//    fun checkApkVersion(currentVersion: String): Boolean {
//        ULog.d(TAG, "check: $currentVersion, force: $forceUpdateVersion")
//        val updateVersion = forceUpdateVersion
//        return currentVersion < updateVersion
//    }

    companion object {
        private const val TAG = "ScanViewModel"
    }
}