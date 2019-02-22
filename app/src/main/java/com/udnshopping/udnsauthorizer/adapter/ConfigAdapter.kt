package com.udnshopping.udnsauthorizer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udnshopping.udnsauthorizer.R

class ConfigAdapter(items: Map<String, String>?, private val context: Context) :
    RecyclerView.Adapter<ConfigAdapter.ViewHolder>() {

    private var keyList: List<String> = items?.keys?.toList() ?: listOf()
    private var valueList: List<String> = items?.values?.toList() ?: listOf()

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val tvConfigName: TextView = view.findViewById(R.id.tv_config_name)
        val tvConfigValue: TextView = view.findViewById(R.id.tv_config_value)
    }

    override fun getItemCount(): Int = keyList.size

    // Inflates the item views
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.config_list_item,
                viewGroup,
                false
            )
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (keyList.isNullOrEmpty() || valueList.isNullOrEmpty() || position > keyList.size) {
            return
        }

        // Key
        val name = keyList[position]
        viewHolder.tvConfigName.text = name

        // User and date
        val value = valueList[position]
        viewHolder.tvConfigValue.text = value
    }

    fun updateData(data: Map<String, String>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        Log.d("Config", "update data: $data")
        keyList = data.keys.toList()
        valueList = data.values.toList()
        notifyDataSetChanged()
    }
}
