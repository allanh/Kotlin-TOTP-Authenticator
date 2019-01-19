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
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udnshopping.udnsauthorizer.models.Pin
import com.udnshopping.udnsauthorizer.models.Secret
import com.udnshopping.udnsauthorizer.utils.Logger
import com.udnshopping.udnsauthorizer.utils.ThreeDESUtil
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val secrets: MutableList<Secret> = mutableListOf()
    private val pins: MutableList<Pin> = mutableListOf()
    private val pinMap: MutableMap<String, Pin> = mutableMapOf()
    private val kAuth = "auth"
    private val kSecret = "secret"
    private val kSecretList = "secretList"
    private val kAccount = "acc"
    private var isRefreshing = true
    private val refreshThred = Thread()
    private val kErrorQrcodeMessage = "無效的 QR 碼"
    private val kDone = "確定"
    private val kTime = "time"
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

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
                when (m.action) {
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
        var secret: Secret? = null
        val auth_length = auth?.length ?: 0

        if (auth_length > 0 && (auth?.startsWith("otpauth://totp")) == false) {
            val decryptString = auth?.substring(2)
            try {
                val decrypt = ThreeDESUtil.decrypt(decryptString!!)
                if (!decrypt.isNullOrEmpty() && decrypt.contains("acc", ignoreCase = false)) {
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    val json = decrypt
                    val secretInfo = Gson().fromJson<Map<String, String>>(json, type)
                    Logger.d(TAG, "secretInfo: ${secretInfo?.toString()}")

                    if (!secretInfo.isNullOrEmpty()) {
                        val time = secretInfo[kTime]
                        val timeFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                        val date = timeFormat.parse(time)
                        val dateString = DATE_FORMAT.format(date)

                        secret = Secret(secretInfo[kSecret], secretInfo[kAccount], dateString)
                    }
                } else {
                    errorQRCodeAlert()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorQRCodeAlert()
            }
        } else if (!Uri.parse(auth).getQueryParameter(kSecret).isNullOrEmpty()) {
            val uri = Uri.parse(auth)
            val secretKey = uri.getQueryParameter(kSecret)
            val user = uri.path
            if (!secretKey.isNullOrEmpty() && !user.isNullOrEmpty()) {
                secret = Secret(secretKey, user, "")
            }
        } else {
            errorQRCodeAlert()
        }
        if (secret != null) {
            addSecret(secret)

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
        pinMap.clear()
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
                val pin = Pin(pinString, secret.value, progress, secret.date)
                addPin(pin)
            }
        }

        updatePinListState()
    }

    private fun errorQRCodeAlert() {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setMessage(kErrorQrcodeMessage)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, kDone
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun addPin(pin: Pin) {
        pins += pin
    }

    private fun updatePinListState() {
        for (i in pins.size-1 downTo 0) {
            var key = pins[i].key
            if (!pinMap.containsKey(key)) {
                pinMap[key] = pins[i]
            } else {
                val lastPin = pinMap[key] as Pin
                val lastDate = DATE_FORMAT.parse(lastPin.date)
                val pinDate = DATE_FORMAT.parse(pins[i].date)
                Logger.d(TAG, "LastDate: ${lastDate.time} PinDate: ${pinDate.time}")
                if (lastPin.value == pins[i].value && lastDate.time > pinDate.time) {
                    pins[i].isValid = false
                }
            }
        }
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



