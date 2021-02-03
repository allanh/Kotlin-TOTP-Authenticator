package com.udnshopping.udnsauthorizer.view.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.databinding.FragmentGvScanBinding
import kotlinx.android.synthetic.main.content_scan.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This activity detects QR codes and returns the value with the rear facing camera.
 */
class GVScanFragment : Fragment() {
    private val viewModel: ScanViewModel by viewModel()
    private lateinit var binding: FragmentGvScanBinding

    private var box: Box? = null
    private var isDetected = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentGvScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.startCamera(view.surfaceView.holder)
    }

    override fun onStart() {
        super.onStart()
        isDetected = false
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun initView() {
        box = Box(binding.root.context)
        binding.gvScanMain.removeAllViews()
        binding.gvScanMain.addView(
            box,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
    }

    // TODO: no call
    fun onDetected() {
        isDetected = true
        findNavController().navigateUp()
    }
}