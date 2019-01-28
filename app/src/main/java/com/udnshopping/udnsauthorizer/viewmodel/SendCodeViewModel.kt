package com.udnshopping.udnsauthorizer.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.common.util.IOUtils
import com.udnshopping.udnsauthorizer.utilities.Logger
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.StringWriter
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import javax.net.ssl.HttpsURLConnection

class SendCodeViewModel : ViewModel() {

    fun sendEmail(email: String) {
        GlobalScope.launch {
            val url = URL("$TOTP_API$email")
            val urlConnection = url.openConnection() as HttpsURLConnection

            try {
                val inputStream = BufferedInputStream(urlConnection.getInputStream())
                val buffer = ByteArrayOutputStream()
                var result = inputStream.read()
                while (result != -1) {
                    buffer.write(result.toByte().toInt())
                    result = inputStream.read()
                }
                Logger.d(TAG, "response: ${buffer.toString("UTF-8")}")

                val inp = BufferedInputStream(urlConnection.inputStream)
                inp.close()
            } finally {
                urlConnection.disconnect()
            }
        }
    }

    companion object {

        private const val TAG = "SendCodeViewModel"

        private const val TOTP_API = "https://uat-shopping56.udn.com/spm/TOTPGenQrcodeMail.do?source="

    }
}