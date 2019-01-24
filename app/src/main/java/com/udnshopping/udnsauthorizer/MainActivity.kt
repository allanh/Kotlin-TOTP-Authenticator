package com.udnshopping.udnsauthorizer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.udnshopping.udnsauthorizer.databinding.ActivityMainBinding
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory

class MainActivity : AppCompatActivity() {

    private var isRefreshing = true
    private val refreshThred = Thread()
    private val kErrorQrcodeMessage = "無效的 QR 碼"
    private val kDone = "確定"


    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var mViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        //setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar?.hide()
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mViewModel = ViewModelProviders.of(this,
            SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        binding.viewModel = mViewModel



        //val host: NavHostFragment = supportFragmentManager
            //.findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        //val navController = host.navController
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController)
/*
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            Toast.makeText(this@MainActivity, "Navigated to $dest",
                Toast.LENGTH_SHORT).show()
            Log.d("NavigationActivity", "Navigated to $dest")
        }
*/
        // Initialize FireBase.
        FirebaseAnalytics.getInstance(this)

        /*
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
        */
    }

//
//    public override fun onPause() {
//        super.onPause()
//        overridePendingTransition(0, 0)
//    }

    override fun onBackPressed() {
//        val fragments = supportFragmentManager.backStackEntryCount
//        Logger.d(TAG, "fragments: $fragments")
//        if (fragments == 1) {
            finish()
//        } else if (fragments > 1) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp();
    }

    override fun onStop() {
        super.onStop()
        mViewModel.saveData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        mViewModel.addData(data.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            goConfig()
//            findNavController(R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_configFragment)
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

    private fun updateUI() {
        //updatePinsAfterClear()
       // my_recycler_view.adapter?.notifyDataSetChanged()
    }

    /*

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
    */
    fun scan() {
        val intent = Intent(this, ScanActivity::class.java).apply {

        }
        startActivityForResult(intent, 1)
    }

    private fun goConfig() {
        val intent = Intent(this, ConfigActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    companion object {

        private const val TAG = "MainActivity"

        // Remote Config keys
    }
}



