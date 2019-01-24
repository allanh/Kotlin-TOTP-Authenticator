package com.udnshopping.udnsauthorizer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.data.Config
import com.udnshopping.udnsauthorizer.extensions.setProgressTintColor

class ConfigAdapter(private val items: Map<String, String>?, private val context: Context) :
    RecyclerView.Adapter<ConfigAdapter.ViewHolder>() {

    var keyList: List<String>

    init {
        keyList = items?.keys?.toList() ?: listOf()
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvConfigName: TextView = view.findViewById(R.id.tv_config_name)
        val tvConfigValue: TextView = view.findViewById(R.id.tv_config_value)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    // Inflates the item views
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.config_list_item, viewGroup, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (items == null || position > items.size) {
            return
        }

        // Key
        val name = keyList[position]
        viewHolder.tvConfigName.text = name

        // User and date
        val value = items[name]
        viewHolder.tvConfigValue.text = value
    }
}
