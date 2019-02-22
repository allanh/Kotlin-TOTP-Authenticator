package com.udnshopping.udnsauthorizer.view.sendcode

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.FragmentSendCodeBinding
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.model.KeyUpEvent
import dagger.android.support.AndroidSupportInjection
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class SendCodeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SendCodeViewModel
    private lateinit var binding: FragmentSendCodeBinding

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SendCodeViewModel::class.java)
        viewModel.getResultObservable().observe(this, Observer {
            ULog.d(TAG, "result: $it")
            activity?.onBackPressed()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ULog.d(TAG, "onCreate")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_code, container, false)
        binding.viewModel = viewModel
        EventBus.getDefault().register(this)
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
    @Subscribe
    @Suppress("unused")
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

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
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