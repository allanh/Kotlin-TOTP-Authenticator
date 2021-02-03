package com.udnshopping.udnsauthorizer.view.pins

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.udnshopping.udnsauthorizer.model.Pin
import com.udnshopping.udnsauthorizer.utility.ULog
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.adapter.PinAdapter
import com.udnshopping.udnsauthorizer.callback.SwipeToDeleteCallback
import com.udnshopping.udnsauthorizer.adapter.SecretAdapter
import com.udnshopping.udnsauthorizer.callback.SwipeToDeleteCallback2
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
import com.udnshopping.udnsauthorizer.view.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PinsFragment : Fragment() {
    private val pinsViewModel: PinsViewModel by viewModel()
    private lateinit var binding: FragmentPinsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        ULog.d(TAG, "onCreate")
        binding = FragmentPinsBinding.inflate(inflater, container, false)
        binding.viewModel = pinsViewModel
        binding.lifecycleOwner = this
        //ULog.d(TAG, "setListView: ${it.size}")
//        setListView(binding.pinsRecyclerView, pinsViewModel.getPinsObservable().value)

        val adapter = PinAdapter()
        binding.pinsRecyclerView.adapter = adapter

        val swipeToDeleteCallback = object : SwipeToDeleteCallback2(binding.root.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                pinsViewModel.removeAt(pos)
                binding.pinsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.pinsRecyclerView)

        subscribeUI(adapter)
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
        pinsRecyclerView.layoutManager = LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        pinsRecyclerView.adapter = SecretAdapter(pins, fragmentContext)

//        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                ULog.d(TAG, "swiped")
//                pinsViewModel.removeAt(viewHolder.adapterPosition)
//            }
//        }
//        val itemTouchHelper = ItemTouchHelper(swipeHandler)
//        itemTouchHelper.attachToRecyclerView(pinsRecyclerView)


        val swipeToDeleteCallback = object : SwipeToDeleteCallback2(fragmentContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                pinsViewModel.removeAt(pos)
                binding.pinsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(pinsRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        pinsViewModel.enableRefresh(true)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onPause() {
        pinsViewModel.enableRefresh(false)
        super.onPause()
    }

    private fun subscribeUI(adapter: PinAdapter) {
        pinsViewModel.getPinsObservable().observe(viewLifecycleOwner, {
            adapter.submitList(it)
//            binding.pinsRecyclerView.adapter = PinAdapter(it)
//            (binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)
        })
    }

    companion object {
        private const val TAG = "PinsFragment"
    }
}



