package com.udnshopping.udnsauthorizer.di

import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.udnshopping.udnsauthorizer.view.scan.Box
import com.udnshopping.udnsauthorizer.view.scan.DetectorProcessor
import dagger.Provides
import com.udnshopping.udnsauthorizer.view.scan.ScanActivity
import com.udnshopping.udnsauthorizer.view.scan.SurfaceHolderCallback
import dagger.Module
import org.greenrobot.eventbus.EventBus


@Module
class ScanActivityModule {

    @Provides
    fun provideDetectorProcessor(activity: ScanActivity, eventBus: EventBus): DetectorProcessor {
        return DetectorProcessor(activity, eventBus)
    }

    @Provides
    fun provideBarcodeDetector(activity: ScanActivity, detectorProcessor: DetectorProcessor): BarcodeDetector {
        val detector = BarcodeDetector.Builder(activity)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        detector.setProcessor(detectorProcessor)
        return detector
    }

    @Provides
    fun provideCameraSource(activity: ScanActivity, detector: BarcodeDetector): CameraSource {
        return CameraSource.Builder(activity, detector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
    }

    @Provides
    fun provideSurfaceHolderCallback(cameraSource: CameraSource): SurfaceHolderCallback {
        return SurfaceHolderCallback(cameraSource)
    }
}