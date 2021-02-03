package com.udnshopping.udnsauthorizer.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.udnshopping.udnsauthorizer.utility.ULog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * QRCodeRepository provides an interface to send an email to TOTP server.
 */
@Suppress("unused")
class QRCodeRepository constructor(private val context: Context) {

    /**
     * This is the job for all coroutines started by this repository.
     *
     * Cancelling this job will cancel all coroutines started by this repository.
     */
    private val job = Job()

    /**
     * This is the main scope for all coroutines launched by QRCodeRepository.
     *
     * Since we pass job, you can cancel all coroutines launched by uiScope by calling
     * job.cancel()
     */
    private val scope = CoroutineScope(Dispatchers.Main + job)

    var result = MutableLiveData<String>()

    private fun sendEmailToTOTP(email: String): String? {
        var result: String? = null
        val url = URL("${getTotpApi()}$email")
        val urlConnection = url.openConnection() as HttpsURLConnection

        try {
            // Sets the SSLSocketFactory
//            val input = activity?.resources?.openRawResource(R.raw.udn)
//            input?.let {
//                ULog.d(TAG, "input: ${input.available()}")
//                UdnSSLContextFactory.getSSLContext(it)?.let { context ->
//                    urlConnection.sslSocketFactory = context.socketFactory
//                }
//            }
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val buffer = ByteArrayOutputStream()
            var resultStream = inputStream.read()
            while (resultStream != -1) {
                buffer.write(resultStream.toByte().toInt())
                resultStream = inputStream.read()
            }
            result = buffer.toString("UTF-8")
            ULog.d(TAG, "result: $result")

            inputStream.close()
        } catch (exception: Exception) {
            ULog.d(TAG, "error message: ${exception.message}")
            result = exception.localizedMessage?.toString()
        } finally {
            urlConnection.disconnect()
            ULog.d(TAG, "finally")
            return result
        }
    }

    //    val hostnameVerifier = HostnameVerifier { _, session ->
//        HttpsURLConnection.getDefaultHostnameVerifier().run {
//            verify("udn.com", session)
//        }
//    }

    fun sendEmail(email: String) {
        scope.launch(Dispatchers.IO) {
            result.postValue(sendEmailToTOTP(email))
        }
    }

    fun cancel() {
        job.cancel()
    }

    private external fun getTotpApi(): String

    companion object {

        private const val TAG = "QRCodeRepository"

        init {
            try {
                System.loadLibrary("keys")
            } catch (e: UnsatisfiedLinkError) {
                // only ignore exception in non-android env
                if ("Dalvik" == System.getProperty("java.vm.name")) throw e
            }
        }
    }
}
