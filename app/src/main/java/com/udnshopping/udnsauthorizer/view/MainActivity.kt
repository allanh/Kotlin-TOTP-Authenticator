package com.udnshopping.udnsauthorizer.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.databinding.ActivityMainBinding
import com.udnshopping.udnsauthorizer.extension.getCurrentFragmentId
import com.udnshopping.udnsauthorizer.extension.isCurrentFragment
import com.udnshopping.udnsauthorizer.model.KeyUpEvent
import com.udnshopping.udnsauthorizer.utility.ULog
import com.udnshopping.udnsauthorizer.viewmodel.MainActivityViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var mViewModel: SharedViewModel
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        ULog.d(TAG, "onCreate")

        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )

        //setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar?.hide()
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
//        mainViewModel.isForceUpdateObservable().observe(this, Observer {
//            //            if (it) {
//            ULog.d(TAG, "new isForceUpdate: $it")
//            if (mainViewModel.checkApkVersion(getVersion())) {
//                showUpdateDialog()
//            }
////            }
//        })
        mainViewModel.fetchRemoteConfig()

        mViewModel = ViewModelProviders.of(this,
            SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        mViewModel.showQRCodeErrorEvent.observe(this, Observer {
            ULog.d(TAG, "show qr code error")
            errorQRCodeAlert()
        })
        binding.viewModel = mViewModel
        ULog.d(TAG, "onCreate done")
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onBackPressed() {
        when (getCurrentFragmentId(R.id.nav_host_fragment)) {
            R.id.mainFragment, R.id.pinsFragment -> {
                mViewModel.saveData()
                finish()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        EventBus.getDefault().post(KeyUpEvent(keyCode))
        return super.onKeyUp(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp();
    }

    override fun onResume() {
        super.onResume()
        if (!isCurrentFragment(R.id.nav_host_fragment, R.id.mainFragment)
            && mViewModel.isDataEmpty.get()) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.saveData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SCAN_QR_CODE -> {
                if (data == null) {
                    return
                }
                mViewModel.addData(data.extras)
                findNavController(R.id.nav_host_fragment).navigate(R.id.pinsFragment)
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

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.update_dialog_title)
            .setMessage(getString(R.string.update_dialog_content))
            .setNegativeButton(R.string.update) { dialog, _ ->
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

    private fun getVersion(): String {
        try {
            val pInfo = getPackageManager()?.getPackageInfo(packageName, 0)
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

    companion object {

        private const val TAG = "MainActivity"

        private const val CAMERA_REQUEST_CODE = 1

        private const val SCAN_QR_CODE = 2

    }
}



