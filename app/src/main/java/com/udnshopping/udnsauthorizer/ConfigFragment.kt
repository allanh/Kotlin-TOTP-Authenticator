package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.udnshopping.udnsauthorizer.data.Config
import androidx.recyclerview.widget.RecyclerView


class ConfigFragment  : Fragment() {

    lateinit var configs: Config

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configs = Config(mapOf("version" to "1.0.3"))
        setHasOptionsMenu(true);

        val rootView = inflater.inflate(R.layout.activity_config, container, false)
        val recyclerView = rootView.findViewById(R.id.config_recycler_view) as RecyclerView

        // Creates a vertical Layout Manager
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        recyclerView.adapter = ConfigAdapter(configs.config, activity as Context)
        recyclerView.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                activity,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )

        return rootView;
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
}