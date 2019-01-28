package com.udnshopping.udnsauthorizer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import com.udnshopping.udnsauthorizer.viewmodel.MainViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory


class MainFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d(TAG, "onCreate")

        var binding =
            DataBindingUtil.inflate<FragmentMainBinding>(inflater,
                R.layout.fragment_main, container, false)
        mViewModel = MainViewModel(activity)
        binding.viewModel = mViewModel

        binding.buttonScan.setOnClickListener {
            if (sharedViewModel.isDataEmpty.get()) {
                (activity as MainActivity).checkPermission()
            } else {
                findNavController().navigate(R.id.pinsFragment)
            }
        }

        binding.buttonEmail.setOnClickListener {
            findNavController().navigate(R.id.sendCodeFragment)
        }

        sharedViewModel = activity?.run {
            ViewModelProviders.of(this, SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        Logger.d(TAG, "onCreate done")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()

        if (!sharedViewModel.isDataEmpty.get()) {
            findNavController().navigate(R.id.pinsFragment)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {

        private const val TAG = "MainFragment"

    }
}