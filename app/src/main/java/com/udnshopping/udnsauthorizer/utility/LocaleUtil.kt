package com.udnshopping.udnsauthorizer.utility

import android.content.Context
import android.os.Build
import java.util.*

object LocaleUtil {
    /**
     * Get the current locale.
     */
    @Suppress("DEPRECATION")
    fun getCurrent(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            // This field was deprecated in API level 24
            context.resources.configuration.locale
        }
    }
}