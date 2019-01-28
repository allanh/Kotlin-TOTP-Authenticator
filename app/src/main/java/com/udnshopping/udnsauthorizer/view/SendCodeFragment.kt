package com.udnshopping.udnsauthorizer.view

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.FragmentSendCodeBinding
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SendCodeViewModel
import android.view.inputmethod.EditorInfo
import android.widget.TextView




class SendCodeFragment : Fragment() {

    private lateinit var mViewModel: SendCodeViewModel
    private lateinit var mBinding: FragmentSendCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d(TAG, "onCreate")

        mBinding =
            DataBindingUtil.inflate<FragmentSendCodeBinding>(inflater,
                R.layout.fragment_send_code, container, false)
        mViewModel = SendCodeViewModel()
        mBinding.viewModel = mViewModel



        Logger.d(TAG, "onCreate done")
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()

        context?.let {
            openSoftKeyboard(it, mBinding.etEmail)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    private fun openSoftKeyboard(context: Context, view: View) {
        view.requestFocus()
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {

        private const val TAG = "SendCodeFragment"

    }
}