package com.udnshopping.udnsauthorizer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.utility.ULog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val mainViewModel: MainActivityViewModel by viewModel()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        binding.buttonScan.setOnClickListener {
            (activity as MainActivity).checkPermission()
        }

        binding.buttonEmail.setOnClickListener {
            findNavController().navigate(R.id.sendCodeFragment)
        }

        mainViewModel.isDataNotEmptyObservable.observe(viewLifecycleOwner, {
            it?.let { isDataNotEmpty ->
                ULog.d(TAG, "isDataNotEmpty")
                if (isDataNotEmpty) findNavController().navigate(R.id.pinsFragment)
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}