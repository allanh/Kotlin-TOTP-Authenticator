package com.udnshopping.udnsauthorizer

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.udnshopping.udnsauthorizer.databinding.ActivityMainBinding
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory

class PinsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var mViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate")
        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        //setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar?.hide()
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mViewModel = ViewModelProviders.of(this,
            SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        binding.viewModel = mViewModel

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        val navController = host.navController
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController)
/*
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            Toast.makeText(this@PinsActivity, "Navigated to $dest",
                Toast.LENGTH_SHORT).show()
            Log.d("NavigationActivity", "Navigated to $dest")
        }
*/
        // Initialize FireBase.
        FirebaseAnalytics.getInstance(this)

        mViewModel.showQRCodeErrorEvent.observe(this, Observer {
            Logger.d(TAG, "show qr code error")
            errorQRCodeAlert()
        })
        Logger.d(TAG, "onCreate done")
    }

//    override fun onBackPressed() {
//        val fragments = supportFragmentManager.backStackEntryCount
//        Logger.d(TAG, "fragments: $fragments")
//        if (fragments == 1) {
//            finish()
//        } else if (fragments > 1) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp();
    }

    override fun onStop() {
        super.onStop()
        mViewModel.saveData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            SCAN_QR_CODE -> {
                if (data == null) {
                    return
                }
                mViewModel.addData(data.extras)
            }
            else -> { }
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
            //goConfig()
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

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CAMERA)) {
                    showPermissionDialog()
                }
            }
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

    fun scan() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivityForResult(intent, SCAN_QR_CODE)
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@PinsActivity,
                    android.Manifest.permission.CAMERA
                )
            ) {
                // try again to request the permission.
                ActivityCompat.requestPermissions(
                    this@PinsActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@PinsActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
        } else {
            // Permission has already been granted
            scan()
        }
    }

    private fun goSetting() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {

        private const val TAG = "PinsActivity"

        private const val CAMERA_REQUEST_CODE = 1

        private const val SCAN_QR_CODE = 2

    }
}



