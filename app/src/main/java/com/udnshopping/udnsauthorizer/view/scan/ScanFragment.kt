package com.udnshopping.udnsauthorizer.view.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.FragmentScanBinding
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.view.MainActivity
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanFragment : Fragment() {

    private val viewModel: ScanViewModel by viewModel()
    private lateinit var binding: FragmentScanBinding
    private var codeScanner: CodeScanner? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        codeScanner = CodeScanner(binding.root.context, binding.scannerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeScanner?.decodeCallback = DecodeCallback {
            ULog.d(TAG, "decode: " + it.text)
            viewModel.addData(it.text)
            close()
        }

        codeScanner?.errorCallback = ErrorCallback { error ->
            error.printStackTrace()
            runOnUiThread {
                (activity as MainActivity).showErrorDialog(error.message
                        ?: getString(R.string.camera_error)) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        codeScanner?.startPreview()
    }

    override fun onPause() {
        codeScanner?.releaseResources()
        super.onPause()
    }

    private fun close() {
        runOnUiThread {
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val TAG = "ScanFragment"
    }
}



