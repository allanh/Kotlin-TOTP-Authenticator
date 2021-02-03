package com.udnshopping.udnsauthorizer.view.sendcode

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.udnshopping.udnsauthorizer.databinding.FragmentSendCodeBinding
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.model.KeyUpEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class SendCodeFragment : Fragment() {

    private val viewModel: SendCodeViewModel by viewModel()
    private lateinit var binding: FragmentSendCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ULog.d(TAG, "onCreate")
        binding = FragmentSendCodeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getResultObservable().observe(viewLifecycleOwner, Observer {
            ULog.d(TAG, "result: $it")
            activity?.onBackPressed()
        })

        ULog.d(TAG, "onCreate done")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        context?.let {
            openSoftKeyboard(it, binding.etEmail)
        }
    }

    /**
     * Called by eventBus when an event occurs
     */
    fun onKeyEvent(event: KeyUpEvent) {
        when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                val email = binding.etEmail.text.toString()
                if (!email.isEmpty()) {
                    viewModel.sendEmail(email)
                }
            }
            else -> {}
        }
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