package com.udnshopping.udnsauthorizer.view.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.android.material.snackbar.Snackbar
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.model.DetectEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.view.MainActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.longToast

class ScanFragment : Fragment() {

    private var codeScanner: CodeScanner? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        context?.let {
            codeScanner = CodeScanner(it, scannerView)
        }
        codeScanner?.decodeCallback = DecodeCallback {
            ULog.d(TAG, "decode: " + it.text)
            EventBus.getDefault().post(DetectEvent(it.text))
            findNavController().navigateUp()
        }

        codeScanner?.errorCallback = ErrorCallback { error ->
            error.printStackTrace()
            activity?.runOnUiThread {
                (activity as MainActivity)
                    .showErrorDialog(error.message ?: getString(R.string.camera_error))
                    { findNavController().navigateUp() }
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

    companion object {
        private const val TAG = "ScanFragment"
    }
}



