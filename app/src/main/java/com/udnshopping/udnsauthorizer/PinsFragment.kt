package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import com.udnshopping.udnsauthorizer.data.Pin
import com.udnshopping.udnsauthorizer.data.Secret
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import org.apache.commons.codec.binary.Base32
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.udnshopping.udnsauthorizer.databinding.FragmentMainBinding
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController


class PinsFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentPinsBinding
    private var handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding =
            DataBindingUtil.inflate<FragmentPinsBinding>(inflater, R.layout.fragment_pins, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this, SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        Logger.d(TAG, "pin size: ${viewModel.getPinList()?.size}")

        viewModel.pins.observe(this, Observer {
            Logger.d(TAG, "New pin size: ${it.size}")
            if (it?.size == 0) {
                Logger.d(TAG, "back to main")
                findNavController().navigate(R.id.mainFragment)
            } else {
                Logger.d(TAG, "renew list")
                updateListView(viewModel.getPinList())
            }
        })
        binding.viewModel = viewModel
        updateListView(viewModel.getPinList())
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // GoTo config
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_configFragment)
            true
        }

        R.id.action_camera -> {
            // User chose the "Settings" item, show the app settings UI...
            (activity as MainActivity).scan()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun updateListView(pins: MutableList<Pin>?) {
        var fragmentContext = context as Context
        if (fragmentContext == null) {
            return
        }
        // Creates a vertical Layout Manager
        binding.pinsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        binding.pinsRecyclerView.adapter = SecretAdapter(pins, fragmentContext)
        binding.pinsRecyclerView.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                fragmentContext,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )

        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                Logger.d(TAG, "swiped")
                viewModel.removeAt(viewHolder.adapterPosition)
                //val adapter = binding.pinsRecyclerView.adapter as SecretAdapter
                //adapter.removeAt(viewHolder.adapterPosition)
//                secrets.removeAt(viewHolder.adapterPosition)
//                pins.removeAt(viewHolder.adapterPosition)
//                updateUI()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.pinsRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(updateThread, UPDATE_TIME)
    }

    override fun onStop() {
        handler.removeCallbacks(updateThread)
        super.onStop()
    }

    private val updateThread = object: Runnable {
        override fun run() {
            Logger.d(TAG, "update")
            (binding.pinsRecyclerView.adapter as? SecretAdapter)?.updateProgress()
            handler.postDelayed(this, UPDATE_TIME)
        }
    }

    companion object {

        private const val TAG = "PinsFragment"

        private const val UPDATE_TIME = 1000L
    }
}



