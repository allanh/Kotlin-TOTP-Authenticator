package com.udnshopping.udnsauthorizer.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.utility.Logger
import com.udnshopping.udnsauthorizer.utility.singleArgViewModelFactory
import kotlinx.coroutines.*
import java.io.*
import java.lang.Exception
import java.net.URL
import javax.net.ssl.*




class SendCodeViewModel(var activity: Activity?) : ViewModel() {

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var result = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun sendEmail(email: String) {
         scope.launch(Dispatchers.IO) {
            result.postValue(sendEmailToTOTP(email))
        }
    }

    private fun sendEmailToTOTP(email: String) : String? {
        var result: String? = null
        val url = URL("$TOTP_API$email")
        val urlConnection = url.openConnection() as HttpsURLConnection

        try {
            // Sets the SSLSocketFactory
//            val input = activity?.resources?.openRawResource(R.raw.udn)
//            input?.let {
//                Logger.d(TAG, "input: ${input.available()}")
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
            Logger.d(TAG, "result: $result")

            inputStream.close()
        } catch (exception: Exception) {
            Logger.d(TAG, "error message: ${exception.message}")
            result = exception.localizedMessage.toString()
        } finally {
            urlConnection.disconnect()
            Logger.d(TAG, "finally")
            return result
        }
    }

//    val hostnameVerifier = HostnameVerifier { _, session ->
//        HttpsURLConnection.getDefaultHostnameVerifier().run {
//            verify("udn.com", session)
//        }
//    }

    companion object {

        private const val TAG = "SendCodeViewModel"

        private const val TOTP_API = "https://uat-shopping56.udn.com/spm/TOTPGenQrcodeMail.do?source="

        /**
         * Factory for creating [SendCodeViewModel]
         */
        val FACTORY = singleArgViewModelFactory(::SendCodeViewModel)
    }
}