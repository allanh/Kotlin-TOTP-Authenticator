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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class MainFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainActivityViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
        mainViewModel.isDataEmptyObservable.observe(this, Observer {
            it?.let { isDataEmpty ->
                ULog.d(TAG, "isDataEmpty")
                if (!isDataEmpty) findNavController().navigate(R.id.pinsFragment)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ULog.d(TAG, "onCreate")
        val binding =
            DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false)
        binding.fragment = this
        binding.viewModel = mainViewModel
        binding.setLifecycleOwner(this)

        ULog.d(TAG, "onCreate done")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    fun onScanClick() {
        (activity as MainActivity).checkPermission()
    }

    fun onEmailClick() {
        findNavController().navigate(R.id.sendCodeFragment)
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}