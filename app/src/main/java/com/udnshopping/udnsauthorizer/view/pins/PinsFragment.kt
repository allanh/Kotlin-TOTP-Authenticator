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
import com.udnshopping.udnsauthorizer.callback.SwipeToDeleteCallback
import com.udnshopping.udnsauthorizer.adapter.SecretAdapter
import com.udnshopping.udnsauthorizer.databinding.FragmentPinsBinding
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

        setListView(binding.pinsRecyclerView, pinsViewModel.getPinsObservable().value)
        subscribeUI()
        ULog.d(TAG, "onCreate done")
        return binding.root
    }

    private fun setListView(pinsRecyclerView: RecyclerView, pins: MutableList<Pin>?) {
        val fragmentContext = context as Context

        // Creates a vertical Layout Manager
        pinsRecyclerView.layoutManager = LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        pinsRecyclerView.adapter = SecretAdapter(pins, fragmentContext)

        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                ULog.d(TAG, "swiped")
                pinsViewModel.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
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

    private fun subscribeUI() {
        pinsViewModel.getPinsObservable().observe(viewLifecycleOwner, {
            (binding.pinsRecyclerView.adapter as SecretAdapter).updateData(it)
        })
    }

    companion object {
        private const val TAG = "PinsFragment"
    }
}



