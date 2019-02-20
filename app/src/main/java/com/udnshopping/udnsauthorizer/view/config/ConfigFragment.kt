package com.udnshopping.udnsauthorizer.view.config

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.model.Config
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.udnshopping.udnsauthorizer.BuildConfig
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.adapter.ConfigAdapter
import com.udnshopping.udnsauthorizer.databinding.FragmentConfigBinding
import com.udnshopping.udnsauthorizer.utility.ULog


class ConfigFragment  : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var binding =
                DataBindingUtil.inflate<FragmentConfigBinding>(inflater,
                    R.layout.fragment_config, container, false)

        var configs = Config(mapOf("version" to BuildConfig.VERSION_NAME))

        // Creates a vertical Layout Manager
        binding.configRecyclerView.layoutManager = LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        binding.configRecyclerView.adapter =
                ConfigAdapter(configs.config, activity as Context)

        binding.configToolbar.setNavigationOnClickListener {
            ULog.d(TAG, "Navigation click")
            activity?.supportFragmentManager?.popBackStack()
        }

//        (activity as AppCompatActivity).setSupportActionBar(binding.configToolbar)
//        (activity as AppCompatActivity).supportActionBar?.title = "config"
//        setHasOptionsMenu(true);
        return binding.root;
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        ULog.d(TAG, "onCreateOptionMenu")
        menu.clear()
        inflater.inflate(R.menu.toolbar_config, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // GoTo config
            findNavController().navigate(R.id.pinsFragment)
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_configFragment)
            true
        }

        R.id.action_camera -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    companion object {

        private const val TAG = "ConfigFragment"

    }
}