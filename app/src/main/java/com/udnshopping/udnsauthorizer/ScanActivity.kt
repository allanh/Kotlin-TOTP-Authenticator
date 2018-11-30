package com.udnshopping.udnsauthorizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import android.view.View

class ScanActivity : AppCompatActivity() {
    private var cameraView: SurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@ScanActivity,
                    android.Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@ScanActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    0
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            startCamera()
        }
    }

    fun startCamera() {
        setContentView(R.layout.activity_scan)
        cameraView = findViewById<SurfaceView>(R.id.surfaceView) as SurfaceView
        val detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        println(cameraView?.holder)
        cameraView?.holder?.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(cameraView?.holder)
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
            }

            override
            fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    val intent = Intent(this@ScanActivity, MainActivity::class.java)
                    intent.putExtra("auth", barcodes.valueAt(0).displayValue)
                    setResult(1, intent)
                    this@ScanActivity.finish()
                }
            }
        });

        if (!detector!!.isOperational) {
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startCamera()
    }
}