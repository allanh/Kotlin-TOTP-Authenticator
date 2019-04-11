package com.udnshopping.udnsauthorizer.view.scan

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.udnshopping.udnsauthorizer.model.DetectEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class DetectorProcessor @Inject
constructor(private var fragment: GVScanFragment, private val eventBus: EventBus) : Detector.Processor<Barcode> {

    override
    fun receiveDetections(detections: Detector.Detections<Barcode>) {
        val barCodes = detections.detectedItems
        if (barCodes.size() != 0) {
            val barcode = barCodes.valueAt(0)
            val box = fragment.box
            val rectangle: Rect? = box?.getRectangle()
            val metaData = detections.frameMetadata
            val matrix = Matrix()
            box?.let {
                matrix.setScale(
                    (it.right - it.left).toFloat() / metaData.width,
                    (it.bottom - it.top).toFloat() / metaData.height
                )
            }

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
                    ULog.d(TAG, "isDetected: " + fragment.isDetected)
                    if (!fragment.isDetected) {
                        fragment.onDetected()
                        val auth = barCodes.valueAt(0).displayValue
                        eventBus.post(DetectEvent(auth))
                    }
                }
            }
        }
    }

    override
    fun release() {
        ULog.d(TAG, "release")
    }

    companion object {
        private const val TAG = "DetectorProcessor"
    }
}