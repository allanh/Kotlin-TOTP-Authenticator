package com.udnshopping.udnsauthorizer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.apache.commons.codec.binary.Base32
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem
import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    // Initializing an empty ArrayList to be filled with animals
    private val secrets: MutableList<PIN> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pins = getPinList()
        secrets += pins
        adapteSecret()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val auth = data?.extras?.getString("auth")
        val uri = Uri.parse(auth)
        val secret = uri.getQueryParameter("secret")
        val user = uri.path
        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30, timeStepUnit = java.util.concurrent.TimeUnit.SECONDS
        )
        val timeBasedOneTimePasswordGenerator = TimeBasedOneTimePasswordGenerator(Base32().decode(secret), config)
        val pin: PIN = PIN(timeBasedOneTimePasswordGenerator.generate(), user)
        addSecret(pin)
        //--SAVE Data
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val type = object : TypeToken<List<PIN>>() {}.type
        val json = Gson().toJson(secrets, type)
        editor.putString("PINs", json).apply()
        editor.commit()

        adapteSecret()
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

    data class PIN(
        @SerializedName("secret") private val _key: String?,
        @SerializedName("user") private val _value: String?
    ) {
        val key: String
            get() = _key ?: ""

        val value: String
            get() = _value ?: ""
    }

    fun getPinList(): List<PIN> {
        val type = object : TypeToken<List<PIN>>() {}.type
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val json = preferences.getString("PINs", "")
        return if (json.isNotBlank()) Gson().fromJson(json, type) else listOf()
    }

    fun adapteSecret() {
        // Creates a vertical Layout Manager
        my_recycler_view.layoutManager = LinearLayoutManager(this)
        // Access the RecyclerView Adapter and load the data into it
        my_recycler_view.adapter = SecretAdapter(secrets, this)
    }

    fun scan() {
        val intent = Intent(this, ScanActivity::class.java).apply {

        }
        startActivityForResult(intent, 1)
    }

    fun addSecret(pin: PIN) {
        secrets += pin
    }
}
