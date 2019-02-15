package com.udnshopping.udnsauthorizer.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.viewmodel.MainActivityViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ULog.d(TAG, "onCreate")

        val binding =
            DataBindingUtil.inflate<FragmentMainBinding>(inflater,
                R.layout.fragment_main, container, false)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.viewModel = mainViewModel
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

        ULog.d(TAG, "onCreate done")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()

        if (!sharedViewModel.isDataEmpty.get()) {
            findNavController().navigate(R.id.pinsFragment)
        }
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}