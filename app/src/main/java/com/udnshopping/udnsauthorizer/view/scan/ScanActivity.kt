package com.udnshopping.udnsauthorizer.view.scan

import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.view.View
import android.view.ViewGroup
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.DetectEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.view.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject
import org.greenrobot.eventbus.EventBus

/**
 * This activity detects QR codes and returns the value with the rear facing camera.
 */
class ScanActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var detector: BarcodeDetector
    @Inject
    lateinit var surfaceHolderCallback: SurfaceHolderCallback
    private var _box: Box? = null
    private var cameraView: SurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        _box = Box(this)
        addContentView(
            _box,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        cameraView = findViewById(R.id.surfaceView)
        startCamera()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun getBox() = _box

    /**
     * Creates and starts the camera.
     */
    private fun startCamera() {
        ULog.d(TAG, "camera view isCreating? ${cameraView?.holder?.isCreating}")

        cameraView?.holder?.addCallback(surfaceHolderCallback)
        if (!detector.isOperational) {
            return
        }
    }

    @Subscribe
    @Suppress("unused")
    fun onDetected(result: DetectEvent) {
        ULog.d(TAG, "receive detect event: ${result.auth}")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("auth", result.auth)
        setResult(1, intent)
        this@ScanActivity.finish()
    }

    companion object {
        private const val TAG = "ScanActivity"
    }
}