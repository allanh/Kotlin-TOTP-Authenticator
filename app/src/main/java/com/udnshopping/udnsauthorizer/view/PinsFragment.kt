package com.udnshopping.udnsauthorizer.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.lifecycle.Observer
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.viewmodel.PinsViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.callback.SwipeToDeleteCallback
import com.udnshopping.udnsauthorizer.adapter.SecretAdapter
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import com.udnshopping.udnsauthorizer.callback.IOnBackPressed
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PinsFragment : Fragment(), IOnBackPressed {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: PinsViewModel
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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PinsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        ULog.d(TAG, "onCreate")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pins, container, false)
        ULog.d(TAG, "pin size: ${viewModel.getPinsObservable().value?.size}")
        viewModel.getPinsObservable().observe(this, Observer {

            (binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)
            if (it.size == 0) {
                if (isPosted) isRefreshing = false
            } else {
                if (!isPosted) isRefreshing = true
            }
        })
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        //ULog.d(TAG, "setListView: ${it.size}")
        setListView(viewModel.getPinsObservable().value)

        ULog.d(TAG, "onCreate done")

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
        val fragmentContext = context as Context

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
                ULog.d(TAG, "swiped")
                viewModel.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.pinsRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        isRefreshing = true
        (activity as AppCompatActivity).supportActionBar?.show()
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



