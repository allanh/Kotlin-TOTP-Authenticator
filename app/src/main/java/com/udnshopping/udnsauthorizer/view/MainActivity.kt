package com.udnshopping.udnsauthorizer.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.udnshopping.udnsauthorizer.BuildConfig
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.ActivityMainBinding
import com.udnshopping.udnsauthorizer.extension.getCurrentFragmentId
import com.udnshopping.udnsauthorizer.extension.isCurrentFragment
import com.udnshopping.udnsauthorizer.model.DetectEvent
import com.udnshopping.udnsauthorizer.model.KeyUpEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainActivityViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ULog.d(TAG, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar?.apply {
            hide()
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        subscribeUI()

        binding.viewModel = mainViewModel
        ULog.d(TAG, "onCreate done")
    }

    override fun onBackPressed() {
        when (getCurrentFragmentId(R.id.nav_host_fragment)) {
            R.id.mainFragment, R.id.pinsFragment -> {
                mainViewModel.saveData()
                finish()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onResume() {
        super.onResume()
        val isForceUpdate = mainViewModel.isForceUpdateObservable.value ?: false
        checkUpdate(isForceUpdate)
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.saveData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            findNavController(R.id.nav_host_fragment).navigate(R.id.configFragment)
            true
        }

        R.id.action_camera -> {
            // User chose the "Settings" item, show the app settings UI...
            checkPermission()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ULog.d(TAG, "get permission")
                GlobalScope.launch {
                    delay(500)
                    scan()
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CAMERA)) {
                    showPermissionDialog()
                }
            }
        }
    }

    private fun subscribeUI() {
        mainViewModel.isForceUpdateObservable.observe(this, Observer {
            checkUpdate(it)
            if (mainViewModel.isShowBroadcast()) {
                showDefaultDialog(mainViewModel.getBroadcastTitle(), mainViewModel.getBroadcastBody())
            }
        })
        mainViewModel.getQRCodeErrorEventObservable().observe(this, Observer {
            errorQRCodeAlert()
        })
        mainViewModel.fetchRemoteConfig()
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.CAMERA
                )
            ) {
                // try again to request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
        } else {
            // Permission has already been granted
            scan()
        }
    }

    fun scan() {
        ULog.d(TAG, "scan")
        runOnUiThread {
            findNavController(R.id.nav_host_fragment).navigate(
                    R.id.scanFragment
                    // Disable the Google Vision Barcode Scanner
//            if (resources.getBoolean(R.bool.isTablet))
//                R.id.scanFragment
//            else
//                R.id.gvScanFragment
            )
        }
    }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.permission_dialog_title)
            .setMessage(getString(R.string.permission_dialog_content))
            .setPositiveButton(R.string.setting) { _, _ ->
                goSetting()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    private fun errorQRCodeAlert() {
        val builder = AlertDialog.Builder(this)
            .setMessage(getString(R.string.qrcode_error))
            .setNeutralButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.update_dialog_title)
            .setCancelable(false)
            .setMessage(getString(R.string.update_dialog_content))
            .setNegativeButton(R.string.update) { _, _ ->
                redirectStore()
            }
        builder.show()
    }

    private fun showDefaultDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    fun showErrorDialog(message: String, func: () -> Unit) {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(R.string.ok) { _, _ ->
                func()
            }
        builder.show()
    }

    private fun getVersion(): String {
        try {
            val pInfo = packageManager?.getPackageInfo(packageName, 0)
            return pInfo?.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    private fun goSetting() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun redirectStore() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(getString(R.string.update_url))
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkUpdate(isForceUpdate: Boolean) {
        ULog.d(TAG, "check isForceUpdate: $isForceUpdate")
        if (isForceUpdate) {
            if (mainViewModel.checkApkVersion(getVersion())) {
                ULog.d(TAG, "show update dialog")
                if (!BuildConfig.DEBUG) showUpdateDialog()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val CAMERA_REQUEST_CODE = 1
    }
}



