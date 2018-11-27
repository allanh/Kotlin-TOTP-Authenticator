package com.udnshopping.udnsauthorizer
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.secret_list_item.view.*
class SecretAdapter(val items : ArrayList<Pair<String, String>>, val context: Context) : RecyclerView.Adapter<SecretAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_secret_type: TextView = v.findViewById(R.id.tv_secret_type)
        val tv_user_type: TextView = v.findViewById(R.id.tv_user_type)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    // Inflates the item views
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.secret_list_item, p0, false))
    }
    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0?.tv_secret_type?.text = items.get(p1).first
        p0?.tv_user_type?.text = items.get(p1).second
    }
}
