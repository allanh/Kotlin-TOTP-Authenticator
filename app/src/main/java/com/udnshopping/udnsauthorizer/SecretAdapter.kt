package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import java.util.*
import java.text.SimpleDateFormat

class SecretAdapter(val items: List<MainActivity.Pin>?, val context: Context) :
    RecyclerView.Adapter<SecretAdapter.ViewHolder>() {
    var handler: Handler? = null

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_secret_type: TextView = v.findViewById(R.id.tv_secret_type)
        val tv_user_type: TextView = v.findViewById(R.id.tv_user_type)
        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
    }

    override fun getItemCount(): Int {
        if (items != null) {
            return items.size
        }
        return 0
    }

    // Inflates the item views
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.secret_list_item, p0, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val df = SimpleDateFormat("ss")
        val second = df.format(Calendar.getInstance().time).toInt()
        var progressStatus = 30 - (100 / 30 * second % 30)

        p0?.tv_secret_type?.text = items?.get(p1)?.key
        p0?.tv_user_type?.text = items?.get(p1)?.value

    }
}
