package com.udnshopping.udnsauthorizer.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.viewmodel.MainActivityViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.content_scan.*
import java.io.IOException
import javax.inject.Inject

class ScanFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var cameraView: SurfaceView
    var box: Box? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initializeUI(view)
    }

//    private fun initializeUI(view: View) {
//        cameraView = surfaceView
//        context?.let {
//            box = Box(it)
//            activity?.addContentView(
//                box,
//                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            )
//            startCamera(it)
//        }
//    }
//
//    fun startCamera(context: Context) {
//
//
//        val detector = BarcodeDetector.Builder(context)
//            .setBarcodeFormats(Barcode.ALL_FORMATS)
//            .build()
//        val cameraSource = CameraSource.Builder(context, detector)
//            .setRequestedPreviewSize(1600, 1024)
//            .setAutoFocusEnabled(true) //you should add this feature
//            .build()
//        println(cameraView?.holder)
//        cameraView?.holder?.addCallback(object : SurfaceHolder.Callback {
//            @SuppressLint("MissingPermission")
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                try {
//                    cameraSource.start(cameraView?.holder)
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
//                cameraSource.stop()
//            }
//        })
//
//
//        detector.setProcessor(object : Detector.Processor<Barcode> {
//            override
//            fun release() {
//            }
//
//            override
//            fun receiveDetections(detections: Detector.Detections<Barcode>) {
//                val barcodes = detections.getDetectedItems();
//                if (barcodes.size() != 0) {
//                    val barcode = barcodes.valueAt(0)
//                    val rectangle: Rect? = this@ScanFragment.box?.rectangle
//                    val metaData = detections.frameMetadata
//                    val matrix = Matrix()
//                    matrix.setScale(
//                        (box!!.right - box!!.left).toFloat() / metaData.width,
//                        (box!!.bottom - box!!.top).toFloat() / metaData.height
//                    )
//                    var boundingBox = RectF(barcode.boundingBox)
//                    matrix.mapRect(boundingBox, RectF(barcode.boundingBox))
//                    rectangle?.let {
//                        if (it.contains(
//                                Rect(
//                                    boundingBox.left.toInt(),
//                                    boundingBox.top.toInt(),
//                                    boundingBox.right.toInt(),
//                                    boundingBox.bottom.toInt()
//                                )
//                            )
//                        ) {
////                            val intent = Intent(this@ScanFragment, MainActivity::class.java)
//                            println(boundingBox)
//                            var auth = barcodes.valueAt(0).displayValue
////                            intent.putExtra("auth", auth)
////                            setResult(1, intent)
////                            this@ScanActivity.finish()
//                        }
//                    }
//                }
//            }
//        })
//
//        if (!detector!!.isOperational) {
//            return
//        }
//    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "ScanFragment"
    }
}