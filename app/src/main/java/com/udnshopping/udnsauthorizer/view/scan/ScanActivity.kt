package com.udnshopping.udnsauthorizer.view.scan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import android.view.View
import android.view.ViewGroup
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.view.MainActivity

/**
 * This activity detects QR codes and returns the value with the rear facing camera.
 */
class ScanActivity : AppCompatActivity() {

    private var cameraView: SurfaceView? = null

    // Region of interest
    private var box: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        box = Box(this)
        addContentView(
            box,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        cameraView = findViewById(R.id.surfaceView)

        startCamera()
    }

    override fun onResume() {
        super.onResume()
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    /**
     * Creates and starts the camera.
     */
    private fun startCamera() {
        ULog.d(TAG, "camera view isCreating? ${cameraView?.holder?.isCreating}")

        val detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        cameraView?.holder?.addCallback(object : SurfaceHolder.Callback {
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
                cameraSource.stop()
            }
        })


        detector.setProcessor(object : Detector.Processor<Barcode> {
            override
            fun release() {
                detector.release()
            }

            override
            fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barCodes = detections.detectedItems
                if (barCodes.size() != 0) {
                    val barcode = barCodes.valueAt(0)
                    val rectangle: Rect? = this@ScanActivity.box?.rectangle
                    val metaData = detections.frameMetadata
                    val matrix = Matrix()
                    matrix.setScale(
                        (box!!.right - box!!.left).toFloat() / metaData.width,
                        (box!!.bottom - box!!.top).toFloat() / metaData.height
                    )
                    val boundingBox = RectF(barcode.boundingBox)
                    matrix.mapRect(boundingBox, RectF(barcode.boundingBox))
                    rectangle?.let {
                        if (it.contains(
                                Rect(
                                    boundingBox.left.toInt(),
                                    boundingBox.top.toInt(),
                                    boundingBox.right.toInt(),
                                    boundingBox.bottom.toInt()
                                )
                            )
                        ) {
                            val intent = Intent(this@ScanActivity, MainActivity::class.java)
                            println(boundingBox)
                            val auth = barCodes.valueAt(0).displayValue
                            intent.putExtra("auth", auth)
                            setResult(1, intent)
                            this@ScanActivity.finish()
                        }
                    }
                }
            }
        })

        if (!detector.isOperational) {
            return
        }
    }

    companion object {
        private const val TAG = "ScanActivity"
    }
}