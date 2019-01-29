package com.udnshopping.udnsauthorizer.viewmodel

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udnshopping.udnsauthorizer.repository.QRCodeRepository
import com.udnshopping.udnsauthorizer.utility.Logger
import com.udnshopping.udnsauthorizer.utility.singleArgViewModelFactory
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class SendCodeViewModel(private val repository: QRCodeRepository) : ViewModel() {

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
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var result = MutableLiveData<String>().apply { postValue("") }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun sendEmail(email: String) {
        uiScope.launch {
            result.value = sendEmailToTOTP(email)
        }
    }

    suspend fun sendEmailToTOTP(email: String) : String? {
        val url = URL("${TOTP_API}$email")
        val urlConnection = url.openConnection() as HttpsURLConnection
        var result: String? = null

        try {
            val inputStream = BufferedInputStream(urlConnection.getInputStream())
            val buffer = ByteArrayOutputStream()
            var resultStream = inputStream.read()
            while (resultStream != -1) {
                buffer.write(resultStream.toByte().toInt())
                resultStream = inputStream.read()
            }
            result = buffer.toString("UTF-8")
            Logger.d(TAG, "response: }")

            inputStream.close()
        } catch (exception: Exception) {
            Logger.d(TAG, "error message: ${exception.toString()}")
            result = exception.localizedMessage.toString()
        } finally {
            urlConnection.disconnect()
            Logger.d(TAG, "finally")
            return result
        }
    }

    companion object {

        private const val TAG = "SendCodeViewModel"

        private const val TOTP_API = "https://uat-shopping56.udn.com/spm/TOTPGenQrcodeMail.do?source="

        /**
         * Factory for creating [SendCodeViewModel]
         *
         * @param arg the repository to pass to [SendCodeViewModel]
         */
        val FACTORY = singleArgViewModelFactory(::SendCodeViewModel)
    }
}