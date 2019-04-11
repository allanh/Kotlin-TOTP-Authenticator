package com.udnshopping.udnsauthorizer.view.scan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.DetectEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.content_scan.*
import kotlinx.android.synthetic.main.content_scan.view.*
import kotlinx.android.synthetic.main.fragment_gv_scan.*
import kotlinx.android.synthetic.main.fragment_gv_scan.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


/**
 * This activity detects QR codes and returns the value with the rear facing camera.
 */
class GVScanFragment : Fragment() {
    @Inject
    lateinit var viewModel: ScanViewModel
    var box: Box? = null
    var isDetected = false

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        box = Box(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gv_scan, container, false)
        view.gv_scan_main.addView(
            box,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        viewModel.startCamera(view.surfaceView.holder)
        return view
    }

    override fun onStart() {
        super.onStart()
        isDetected = false
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    fun onDetected() {
        isDetected = true
        findNavController().navigateUp()
    }

    companion object {
        private const val TAG = "GVScanFragment"
    }
}