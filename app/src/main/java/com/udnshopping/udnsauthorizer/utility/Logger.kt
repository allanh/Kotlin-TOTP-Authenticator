package com.udnshopping.udnsauthorizer.utility

import android.util.Log
import com.udnshopping.udnsauthorizer.BuildConfig

/**
 * Instead of directly using Log.d(…), encapsulate it around a static function of a wrapper class.
 * And then leverage the BuildConfig.DEBUG build type variable that you could use to determine if
 * you code is running in DEBUG or RELEASE build.
 */
object Logger {
    fun e(tag: String, message: String?) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message)
    }

    fun w(tag: String, message: String?) {
        if (BuildConfig.DEBUG)
            Log.w(tag, message)
    }

    fun i(tag: String, message: String?) {
        if (BuildConfig.DEBUG)
            Log.i(tag, message)
    }

    fun d(tag: String, message: String?) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message)
    }

    fun v(tag: String, message: String?) {
        if (BuildConfig.DEBUG)
            Log.v(tag, message)
    }
}