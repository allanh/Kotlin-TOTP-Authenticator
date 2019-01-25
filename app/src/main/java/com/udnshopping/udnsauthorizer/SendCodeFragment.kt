package com.udnshopping.udnsauthorizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import com.udnshopping.udnsauthorizer.databinding.FragmentSendCodeBinding
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.MainViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory


class SendCodeFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d(TAG, "onCreate")

        var binding =
            DataBindingUtil.inflate<FragmentSendCodeBinding>(inflater, R.layout.fragment_send_code, container, false)
        mViewModel = MainViewModel(activity)
        binding.viewModel = mViewModel

        Logger.d(TAG, "onCreate done")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    companion object {

        private const val TAG = "SendCodeFragment"

    }
}