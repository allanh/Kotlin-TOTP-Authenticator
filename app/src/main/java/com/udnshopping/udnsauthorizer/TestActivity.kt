package com.udnshopping.udnsauthorizer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.udnshopping.udnsauthorizer.utilities.Logger
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udnshopping.udnsauthorizer.utilities.ThreeDESUtil
import java.text.SimpleDateFormat


class TestActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var isEmailInput = true

    private val kAuth = "auth"
    private val kSecret = "secret"
    private val kAccount = "acc"
    private val kErrorQrcodeMessage = "無效的 QR 碼"
    private val kDone = "確定"
    private val kTime = "time"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get the default actionbar instance
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowHomeEnabled(false)
        actionBar?.setDisplayShowTitleEnabled(false)

        //Initializes the custom action bar layout
        val mInflater = LayoutInflater.from(this)
        val mCustomView = mInflater.inflate(R.layout.nav_header, null)
        actionBar?.setCustomView(mCustomView)
        actionBar?.setDisplayShowCustomEnabled(true)

        //Detect the button click event of the home button in the actionbar
        //Detect the button click event of the home button in the actionbar
        val versionButton = findViewById<View>(R.id.version_button) as ImageButton
        versionButton.setOnClickListener {
            scan()
        }

        val scanButton = findViewById<View>(R.id.scan_button) as ImageButton
        scanButton.setOnClickListener {
            scan()
        }

        FirebaseAnalytics.getInstance(this)

        // Initialize FireBase.
        initRemoteConfig()
        fetchEmailInputConfig()
        //goMainFragment()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        /*
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
            editor.putString(kSecretList, json)?.apply()
            editor.commit()

            updatePinsAfterClear()
            my_recycler_view.adapter?.notifyItemInserted(secrets.size)
        }
        */
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

    private fun scan() {
        val intent = Intent(this, ScanActivity::class.java).apply {

        }
        startActivityForResult(intent, 1)
    }

    private fun errorQRCodeAlert() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage(kErrorQrcodeMessage)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, kDone
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

//    private fun goMainFragment() {
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.main_fragment, MainFragment.newInstance())
//        ft.commit()
//    }

    companion object {

        private const val TAG = "TestActivity"

        // Remote Config keys
        private const val EMAIL_INPUT_CONFIG_KEY = "email_input_enabled"
    }
}