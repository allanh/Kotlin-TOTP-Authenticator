package com.udnshopping.udnsauthorizer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.apache.commons.codec.binary.Base32
import java.util.*
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    private var textResults: TextView? = null

    // Initializing an empty ArrayList to be filled with animals
    val secrets: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textResults = findViewById<TextView>(R.id.textView2) as TextView

        // Creates a vertical Layout Manager
        my_recycler_view.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        my_recycler_view.adapter = SecretAdapter(secrets, this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val auth = data?.extras?.getString("auth")

        val uri = Uri.parse(auth)


        val secret = uri.getQueryParameter("secret")
        val config = TimeBasedOneTimePasswordConfig(codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30, timeStepUnit = java.util.concurrent.TimeUnit.SECONDS)
        val timeBasedOneTimePasswordGenerator = TimeBasedOneTimePasswordGenerator(Base32().decode(secret), config)
        textResults?.setText(timeBasedOneTimePasswordGenerator.generate())
        addSecret(timeBasedOneTimePasswordGenerator.generate())

        // Creates a vertical Layout Manager
        my_recycler_view.layoutManager = LinearLayoutManager(this)

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        my_recycler_view.adapter = SecretAdapter(secrets, this)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_camera -> {
            // User chose the "Settings" item, show the app settings UI...
            scan()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    fun scan() {
        val intent = Intent(this, ScanActivity::class.java).apply {

        }
        startActivityForResult(intent, 1)
    }
    fun addSecret(secret: String) {
        secrets.add(secret)
    }
}
