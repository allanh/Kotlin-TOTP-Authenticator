package com.udnshopping.udnsauthorizer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.extension.setProgressTintColor
import com.udnshopping.udnsauthorizer.model.Pin

class SecretAdapter(private var items: List<Pin>?, private val context: Context) :
    RecyclerView.Adapter<SecretAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSecretType: TextView = view.findViewById(R.id.secret_key_text_view)
        val tvUserType: TextView = view.findViewById(R.id.secret_user_text_view)
        val tvDate: TextView = view.findViewById(R.id.secret_date_text_view)
        val progressBar: ProgressBar = view.findViewById(R.id.secret_progress_bar)
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
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        items?.let {
            // Key
            viewHolder.tvSecretType.text = it[position].key

            // User and date
            val user = it[position].user.removePrefix("/UDN:")
            viewHolder.tvUserType.text = user
            viewHolder.tvDate.text = it[position].date

            // Progress
            val progress = (60 - (it[position].progress % 60)) * 100 / 60
            //ULog.d("adapter", "progress: $progress")
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
        if (data.isNullOrEmpty()) {
            return
        }
        items = data
        notifyDataSetChanged()
    }
}
