package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udnshopping.udnsauthorizer.data.Config
import androidx.recyclerview.widget.RecyclerView
import android.text.method.TextKeyListener.clear
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.udnshopping.udnsauthorizer.databinding.FragmentConfigBinding
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import com.udnshopping.udnsauthorizer.utilities.Logger
import kotlinx.android.synthetic.main.fragment_config.*


class ConfigFragment  : Fragment() {

    lateinit var configs: Config

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var binding =
                DataBindingUtil.inflate<FragmentConfigBinding>(inflater, R.layout.fragment_config, container, false)

        var configs = Config(mapOf("version" to BuildConfig.VERSION_NAME))

        // Creates a vertical Layout Manager
        binding.configRecyclerView.layoutManager = LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        binding.configRecyclerView.adapter = ConfigAdapter(configs.config, activity as Context)

        binding.configToolbar.setNavigationOnClickListener {
            Logger.d(TAG, "Navigation click")
            activity?.supportFragmentManager?.popBackStack()
        }

//        (activity as AppCompatActivity).setSupportActionBar(binding.configToolbar)
//        (activity as AppCompatActivity).supportActionBar?.title = "config"
//        setHasOptionsMenu(true);
        return binding.root;
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Logger.d(TAG, "onCreateOptionMenu")
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

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    companion object {

        private const val TAG = "ConfigFragment"

    }
}