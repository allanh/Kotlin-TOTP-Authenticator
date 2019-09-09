package com.udnshopping.udnsauthorizer.view.pins

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.lifecycle.Observer
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.utility.ULog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.callback.SwipeToDeleteCallback
import com.udnshopping.udnsauthorizer.adapter.SecretAdapter
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import com.udnshopping.udnsauthorizer.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PinsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: PinsViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PinsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        ULog.d(TAG, "onCreate")
        val binding = DataBindingUtil.inflate<FragmentPinsBinding>(inflater,
            R.layout.fragment_pins, container, false)
        viewModel.getPinsObservable().observe(this, Observer {
            (binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)
        })
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //ULog.d(TAG, "setListView: ${it.size}")
        setListView(binding.pinsRecyclerView, viewModel.getPinsObservable().value)

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

    private fun setListView(pinsRecyclerView: RecyclerView, pins: MutableList<Pin>?) {
        val fragmentContext = context as Context

        // Creates a vertical Layout Manager
        pinsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        pinsRecyclerView.adapter = SecretAdapter(pins, fragmentContext)

        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                ULog.d(TAG, "swiped")
                viewModel.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(pinsRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        viewModel.enableRefresh(true)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onPause() {
        viewModel.enableRefresh(false)
        super.onPause()
    }

    companion object {
        private const val TAG = "PinsFragment"
    }
}



