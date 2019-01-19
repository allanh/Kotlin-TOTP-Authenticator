package com.udnshopping.udnsauthorizer

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.udnshopping.udnsauthorizer.extensions.setProgressTintColor
import java.text.ParseException
import java.text.SimpleDateFormat

class SecretAdapter(private val items: List<MainActivity.Pin>?, private val context: Context) :
    RecyclerView.Adapter<SecretAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSecretType: TextView = view.findViewById(R.id.tv_secret_type)
        val tvUserType: TextView = view.findViewById(R.id.tv_user_type)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    // Inflates the item views
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.secret_list_item, viewGroup, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (items == null || position > items.size) {
            return
        }

        // Key
        val key = items[position].key
        viewHolder.tvSecretType.text = "${key.substring(0, 3)} ${key.substring(3, 6)}"

        // User and date
        val user = items[position].value.removePrefix("/UDN:")
        viewHolder.tvUserType.text = user
        viewHolder.tvDate.text = items[position].date

        // Progress
        val progress = (30 - (items[position].progress % 30)) * 100 / 30
        viewHolder.progressBar.progress = progress

        // Set color
        val isValid: Boolean = items[position].isValid
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
        viewHolder.progressBar.setProgressTintColor(secretColor)
    }
}
