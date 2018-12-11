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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val secrets: MutableList<Secret> = mutableListOf()
    private val pins: MutableList<Pin> = mutableListOf()
    private val kAuth = "auth"
    private val kSecret = "secret"
    private val kSecretList = "secretList"
    private var isRefreshing = true
    private val refreshThred = Thread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        secrets += getSecretList()

        updatePinsAfterClear()
        adaptSecret()

        fire()
        my_recycler_view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                // Perform tasks here
                when(m.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isRefreshing = false
                        refreshThred.interrupt()
                    }
                    MotionEvent.ACTION_UP -> {
                        isRefreshing = true
                        fire()
                    }
                }
                return false
            }
        })
    }

    override fun onStop() {
        super.onStop()
        //--SAVE Data
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val type = object : TypeToken<List<Secret>>() {}.type
        val json = Gson().toJson(secrets, type)
        editor.putString(kSecretList, json).apply()
        editor.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        val auth = data?.extras?.getString(kAuth)
        val uri = Uri.parse(auth)
        val secretKey = uri.getQueryParameter(kSecret)
        val user = uri.path

        if (secretKey.isNotEmpty() && user.isNotEmpty()) {
            val secret = Secret(secretKey, user)
            addSecret(secret)
        }

        //--SAVE Data
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val type = object : TypeToken<List<Secret>>() {}.type
        val json = Gson().toJson(secrets, type)
        editor.putString(kSecretList, json).apply()
        editor.commit()

        updatePinsAfterClear()
        my_recycler_view.adapter?.notifyItemInserted(secrets.size)
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

    data class Secret(
        @SerializedName("secret") private val _key: String?,
        @SerializedName("user") private val _value: String?
    ) {
        val key: String
            get() = _key ?: ""

        val value: String
            get() = _value ?: ""
    }

    data class Pin(
        @SerializedName("pin") private val _key: String?,
        @SerializedName("user") private val _value: String?,
        @SerializedName("progress") private val _progress: Int?
    ) {
        val key: String
            get() = _key ?: ""

        val value: String
            get() = _value ?: ""

        val progress: Int
            get() = _progress ?: 0
    }

    private fun fire() {
        Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    if (isRefreshing) {
                        runOnUiThread {
                            updateUI()
                        }
                    }
                }
            }
        }).start()
    }


    private fun updateUI() {
        updatePinsAfterClear()
        my_recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun updatePinsAfterClear() {
        pins.clear()
        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30, timeStepUnit = java.util.concurrent.TimeUnit.SECONDS
        )
        for (secret in secrets) {
            if (secret.key.isNotEmpty() && secret.value.isNotEmpty()) {
                val timeBasedOneTimePasswordGenerator =
                    TimeBasedOneTimePasswordGenerator(Base32().decode(secret.key), config)
                val pinString = timeBasedOneTimePasswordGenerator.generate()
                val progress = SimpleDateFormat("ss").format(Calendar.getInstance().time).toInt()
                val pin = Pin(pinString, secret.value, progress)
                addPin(pin)
            }
        }
    }

    private fun addPin(pin: Pin) {
        pins += pin
    }

    private fun getSecretList(): List<Secret> {
        val type = object : TypeToken<List<Secret>>() {}.type
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val json = preferences.getString(kSecretList, "")
        return if (json.isNotBlank()) Gson().fromJson(json, type) else listOf()
    }

    private fun adaptSecret() {
        // Creates a vertical Layout Manager
        my_recycler_view.layoutManager = LinearLayoutManager(this)
        // Access the RecyclerView Adapter and load the data into it
        my_recycler_view.adapter = SecretAdapter(pins, this)
        my_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                secrets.removeAt(viewHolder.adapterPosition)
                pins.removeAt(viewHolder.adapterPosition)
                updateUI()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(my_recycler_view)
    }

    private fun scan() {
        val intent = Intent(this, ScanActivity::class.java).apply {

        }
        startActivityForResult(intent, 1)
    }

    private fun addSecret(secret: Secret) {
        secrets += secret
    }
}



