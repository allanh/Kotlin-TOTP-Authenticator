package com.udnshopping.udnsauthorizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import com.udnshopping.udnsauthorizer.viewmodel.MainViewModel



class MainFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel

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
        return binding.root
    }
}