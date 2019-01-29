package com.udnshopping.udnsauthorizer.adapter

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.extension.setProgressTintColor
import com.udnshopping.udnsauthorizer.model.Pin

class SecretAdapter(private var items: List<Pin>?, private val context: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SecretAdapter.ViewHolder>() {

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvSecretType: TextView = view.findViewById(R.id.tv_secret_type)
        val tvUserType: TextView = view.findViewById(R.id.tv_user_type)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    // Inflates the item views
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.secret_list_item,
                viewGroup,
                false
            )
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        items?.let {
            // Key
            val key = it[position].key
            viewHolder.tvSecretType.text = "${key.substring(0, 3)} ${key.substring(3, 6)}"

            // User and date
            val user = it[position].value.removePrefix("/UDN:")
            viewHolder.tvUserType.text = user
            viewHolder.tvDate.text = it[position].date

            // Progress
            val progress = (60 - (it[position].progress % 60)) * 100 / 60
            //Logger.d("adapter", "progress: $progress")
            viewHolder.progressBar.progress = progress

            // Set color
            val isValid: Boolean = it[position].isValid
            var secretColor = ContextCompat.getColor(context, R.color.holo_orange_dark)
            var userColor = ContextCompat.getColor(context, R.color.textColorPrimary)

            if (!isValid) {
                userColor = Color.LTGRAY
                secretColor = Color.LTGRAY
            } else if (progress < 33) {
                secretColor = Color.RED
            }

            viewHolder.tvSecretType.setTextColor(secretColor)
            viewHolder.tvUserType.setTextColor(userColor)
            viewHolder.tvDate.setTextColor(userColor)
            viewHolder.progressBar.setProgressTintColor(secretColor)
        }
    }

    fun updateData(data: List<Pin>?) {
        if (data == null) {
            return
        }
        items = data
        notifyDataSetChanged()
    }
}
