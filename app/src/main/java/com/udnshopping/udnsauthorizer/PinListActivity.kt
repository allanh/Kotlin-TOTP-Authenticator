package com.udnshopping.udnsauthorizer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.apache.commons.codec.binary.Base32
import android.net.Uri
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udnshopping.udnsauthorizer.data.Pin
import com.udnshopping.udnsauthorizer.data.Secret
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.utilities.ThreeDESUtil
import kotlinx.android.synthetic.main.activity_pins.*
import java.text.SimpleDateFormat
import java.util.*

class PinListActivity : AppCompatActivity() {

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
    private var isEmailInput = true

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pins)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize FireBase.
        FirebaseAnalytics.getInstance(this)
        initRemoteConfig()
        fetchEmailInputConfig()

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
        val auth = data.extras?.getString(kAuth)
        var secret: Secret? = null
        val auth_length = auth?.length ?: 0

        if (auth_length > 0 && (auth?.startsWith("otpauth://totp")) == false) {
            val decryptString = auth.substring(2)
            try {
                val decrypt = ThreeDESUtil.decrypt(decryptString)
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
        android.R.id.home -> {
            scan()
            true
        }

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

    /**
     * Get Remote Config instance and Set default Remote Config parameter values.
     */
    private fun initRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_config_default)
    }

    /**
     * Fetch a email input config from the Remote Config service.
     */
    private fun fetchEmailInputConfig() {

        val isUsingDeveloperMode = remoteConfig.info.configSettings.isDeveloperModeEnabled

        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        val cacheExpiration: Long = if (isUsingDeveloperMode) {
            0
        } else {
            3600 // 1 hour in seconds.
        }

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        remoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    remoteConfig.activateFetched()
                    isEmailInput = remoteConfig.getBoolean(EMAIL_INPUT_CONFIG_KEY)
                    //Logger.d(TAG, "isEmailInput: $isEmailInput")
                } else {
                    Logger.e(TAG, "Fetch Failed")
                }
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
        val alertDialog = AlertDialog.Builder(this@PinListActivity).create()
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
            var key = pins[i].value
            if (!pinMap.containsKey(key)) {
                pinMap[key] = pins[i]
            } else {
                val lastPin = pinMap[key] as Pin
                val lastDate = DATE_FORMAT.parse(lastPin.date)
                val pinDate = DATE_FORMAT.parse(pins[i].date)
                Logger.d(TAG, "LastDate: ${lastDate.time} PinDate: ${pinDate.time}")
                if (lastDate.time > pinDate.time) {
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
        my_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        // Access the RecyclerView Adapter and load the data into it
        my_recycler_view.adapter = SecretAdapter(pins, this)
        my_recycler_view.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                this,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
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

    companion object {

        private const val TAG = "PinListActivity"

        // Remote Config keys
        private const val EMAIL_INPUT_CONFIG_KEY = "email_input_enabled"
    }
}



