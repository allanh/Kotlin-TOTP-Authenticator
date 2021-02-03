package com.udnshopping.udnsauthorizer.adapter


import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.SecretListItemBinding
import com.udnshopping.udnsauthorizer.extension.setProgressTintColor
import com.udnshopping.udnsauthorizer.model.Pin

class PinAdapter : ListAdapter<Pin, RecyclerView.ViewHolder>(PinDiffCallback()) {

//    private var handler = Handler(Looper.getMainLooper())
//    private var isRefreshing = true
//        set(value) {
//            synchronized(value) {
//                if (value) {
//                    handler.postDelayed(updateThread, UPDATE_TIME)
//                    isPosted = true
//                } else if (isPosted) {
//                    handler.removeCallbacks(updateThread)
//                    isPosted = false
//                }
//            }
//        }
//
//    private val updateThread = object: Runnable {
//        override fun run() {
//            updatePins()
//            handler.postDelayed(this, UPDATE_TIME)
//        }
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PinViewHolder(SecretListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PinViewHolder).bind(getItem(position))
    }

    class PinViewHolder(
        private val binding: SecretListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pin) {
            binding.apply {
                pin = item

                // Progress
                val progress = (60 - (item.progress % 60)) * 100 / 60
                secretProgressBar.progress = progress

                var secretColor = ContextCompat.getColor(root.context, R.color.holo_orange_dark)
                var userColor = ContextCompat.getColor(root.context, R.color.textColorPrimary)
                if (!item.isValid) {
                    userColor = Color.LTGRAY
                    secretColor = Color.LTGRAY
                } else if (progress < 33) {
                    secretColor = Color.RED
                }

                secretKeyTextView.setTextColor(secretColor)
                secretProgressBar.setProgressTintColor(secretColor)
                secretUserTextView.setTextColor(userColor)
                secretDateTextView.setTextColor(userColor)
                executePendingBindings()
            }
        }
    }

    companion object {
        private const val UPDATE_TIME = 1000L
    }
}

private class PinDiffCallback : DiffUtil.ItemCallback<Pin>() {

    override fun areItemsTheSame(oldItem: Pin, newItem: Pin): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: Pin, newItem: Pin): Boolean {
        return oldItem == newItem
    }
}