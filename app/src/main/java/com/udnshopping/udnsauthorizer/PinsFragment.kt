package com.udnshopping.udnsauthorizer

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marcelkliemannel.kotlinonetimepassword.HmacAlgorithm
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import com.marcelkliemannel.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import com.udnshopping.udnsauthorizer.data.Pin
import com.udnshopping.udnsauthorizer.data.Secret
import com.udnshopping.udnsauthorizer.utilities.Logger
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModel
import com.udnshopping.udnsauthorizer.viewmodel.SharedViewModelFactory
import org.apache.commons.codec.binary.Base32
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PinsFragment : Fragment() {

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

    private lateinit var recyclerView: RecyclerView
    private lateinit var model: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        model = activity?.run {
            ViewModelProviders.of(this, SharedViewModelFactory(this)).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        Logger.d(TAG, "secret size: ${model.getSecretList().size}")

        val rootView = inflater.inflate(R.layout.fragment_pins, container, false)
        recyclerView = rootView.findViewById<RecyclerView>(R.id.pins_recycler_view)

        recyclerView.setOnTouchListener(object : View.OnTouchListener {
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
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePinsAfterClear()
        adaptSecret()
        fire()
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
                        activity?.runOnUiThread {
                            updateUI()
                        }
                    }
                }
            }
        }).start()
    }


    private fun updateUI() {
        updatePinsAfterClear()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun updatePinsAfterClear() {
        pins.clear()
        pinMap.clear()
        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6, hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30, timeStepUnit = TimeUnit.SECONDS
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
        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = preferences?.getString(kSecretList, "")
        return if (json!!.isNotBlank()) Gson().fromJson(json, type) else listOf()
    }

    private fun adaptSecret() {
        var fragmentContext = context as Context
        if (fragmentContext == null) {
            return
        }
        // Creates a vertical Layout Manager
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        // Access the RecyclerView Adapter and load the data into it
        recyclerView.adapter = SecretAdapter(pins, fragmentContext)
        recyclerView.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                fragmentContext,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )

        val swipeHandler = object : SwipeToDeleteCallback(fragmentContext) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                secrets.removeAt(viewHolder.adapterPosition)
                pins.removeAt(viewHolder.adapterPosition)
                updateUI()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun addSecret(secret: Secret) {
        secrets += secret
    }

    companion object {

        private const val TAG = "PinsFragment"

        // Remote Config keys
        private const val EMAIL_INPUT_CONFIG_KEY = "email_input_enabled"
    }
}



