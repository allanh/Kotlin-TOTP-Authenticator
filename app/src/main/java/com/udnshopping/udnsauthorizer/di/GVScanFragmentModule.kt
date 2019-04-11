package com.udnshopping.udnsauthorizer.di

import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.udnshopping.udnsauthorizer.view.scan.*
import dagger.Provides
import dagger.Module
import org.greenrobot.eventbus.EventBus

@Module
class GVScanFragmentModule {

    @Provides
    fun provideScanViewModel(surfaceHolderCallback: SurfaceHolderCallback): ScanViewModel {
        return ScanViewModel(surfaceHolderCallback)
    }

    @Provides
    fun provideDetectorProcessor(fragment: GVScanFragment, eventBus: EventBus): DetectorProcessor {
        return DetectorProcessor(fragment, eventBus)
    }

    @Provides
    fun provideBarcodeDetector(fragment: GVScanFragment, detectorProcessor: DetectorProcessor): BarcodeDetector {
        val detector = BarcodeDetector.Builder(fragment.context)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        detector.setProcessor(detectorProcessor)
        return detector
    }

    @Provides
    fun provideCameraSource(fragment: GVScanFragment, detector: BarcodeDetector): CameraSource {
        return CameraSource.Builder(fragment.context, detector)
            .setRequestedPreviewSize(1600, 1024)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
    }

    @Provides
    fun provideSurfaceHolderCallback(cameraSource: CameraSource): SurfaceHolderCallback {
        return SurfaceHolderCallback(cameraSource)
    }

    companion object {
        private const val TAG = "GVScanFragmentModule"
    }
}