package com.udnshopping.udnsauthorizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import com.udnshopping.udnsauthorizer.viewmodel.MainViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import androidx.appcompat.app.AppCompatActivity




class MainFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding =
            DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false)
        mViewModel = MainViewModel(activity)
        binding.viewModel = mViewModel

        binding.buttonScan.setOnClickListener {
            (activity as MainActivity).scan()
        }

        binding.buttonEmail.setOnClickListener {
        }

        mSharedViewModel = activity?.run {
            ViewModelProviders.of(this, SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()

        if (!mSharedViewModel.isDataEmpty()) {
            findNavController().navigate(R.id.pinsFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}