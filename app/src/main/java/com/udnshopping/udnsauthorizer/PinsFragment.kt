package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.lifecycle.Observer
import com.udnshopping.udnsauthorizer.data.Pin
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import androidx.databinding.DataBindingUtil
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import com.udnshopping.udnsauthorizer.extensions.IOnBackPressed


class PinsFragment : Fragment(), IOnBackPressed {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentPinsBinding
    private var handler = Handler()
    private var isPosted = false
    private var isRefreshing = true
        set(value) {
            synchronized(value) {
                if (value) {
                    handler.postDelayed(updateThread, UPDATE_TIME)
                    isPosted = true
                } else if (isPosted) {
                    handler.removeCallbacks(updateThread)
                    isPosted = false
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Logger.d(TAG, "onCreate")

        binding =
            DataBindingUtil.inflate<FragmentPinsBinding>(inflater, R.layout.fragment_pins, container, false)

        viewModel = activity?.run {
            ViewModelProviders.of(this, SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        Logger.d(TAG, "pin size: ${viewModel.getPinList()?.size}")
        viewModel.pins.observe(this, Observer {
//            Logger.d(TAG, "New pin size: ${it.size}")

            (binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)

            if (it.size == 0) {
                if (isPosted) isRefreshing = false
                //findNavController().navigate(R.id.mainFragment)
                binding.tvPinsHint.visibility = View.VISIBLE
                binding.pinsRecyclerView.visibility = View.GONE
            } else {
                if (!isPosted) isRefreshing = true
                binding.tvPinsHint.visibility = View.GONE
                binding.pinsRecyclerView.visibility = View.VISIBLE
                //updateListView(viewModel.getPinList())
                //(binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)
            }
        })
        binding.viewModel = viewModel

        //Logger.d(TAG, "setListView: ${it.size}")
        setListView(viewModel.getPinList())

        Logger.d(TAG, "onCreate done")

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

    override fun onBackPressed() = false

    private fun setListView(pins: MutableList<Pin>?) {
        var fragmentContext = context as Context
        if (fragmentContext == null) {
            return
        }

        // Creates a vertical Layout Manager
        binding.pinsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        binding.pinsRecyclerView.adapter = SecretAdapter(pins, fragmentContext)
//        binding.pinsRecyclerView.addItemDecoration(
//            androidx.recyclerview.widget.DividerItemDecoration(
//                fragmentContext,
//                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
//            )
//        )

        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                Logger.d(TAG, "swiped")
                viewModel.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.pinsRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        isRefreshing = true
    }

    override fun onStop() {
        isRefreshing = false
        super.onStop()
    }

    private val updateThread = object: Runnable {
        override fun run() {
            viewModel.updatePins()
            handler.postDelayed(this, UPDATE_TIME)
        }
    }

    companion object {

        private const val TAG = "PinsFragment"

        private const val UPDATE_TIME = 1000L

    }
}



