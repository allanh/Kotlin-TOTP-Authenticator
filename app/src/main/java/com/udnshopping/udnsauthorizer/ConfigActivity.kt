package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.udnshopping.udnsauthorizer.data.Config
import kotlinx.android.synthetic.main.activity_config.*

class  ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        val toolbar = findViewById<Toolbar>(R.id.config_toolbar)
        //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        setSupportActionBar(toolbar)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var configs = Config(mapOf("version" to BuildConfig.VERSION_NAME))

        // Creates a vertical Layout Manager
        config_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        // Access the RecyclerView Adapter and load the data into it
        config_recycler_view.adapter = ConfigAdapter(configs.config, this)
        config_recycler_view.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                this,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_config, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}