package com.udnshopping.udnsauthorizer.repository

import androidx.lifecycle.LiveData
import com.udnshopping.udnsauthorizer.utility.ULog
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * TitleRepository provides an interface to fetch a title or request a new one be generated.
 *
 * Repository modules handle data operations. They provide a clean API so that the rest of the app
 * can retrieve this data easily. They know where to get the data from and what API calls to make
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, in our case it mediates between a network API and an offline database cache.
 */
class QRCodeRepository() {

    /**
     * [LiveData] to load title.
     *
     * This is the main interface for loading a title. The title will be loaded from the offline
     * cache.
     *
     * Observing this will not cause the title to be refreshed, use [TitleRepository.refreshTitle]
     * to refresh the title.
     *
     * Because this is defined as `by lazy` it won't be instantiated until the property is
     * used for the first time.
     */
//    val title: LiveData<String> by lazy<LiveData<String>>(LazyThreadSafetyMode.NONE) {
//        //Transformations.map(titleDao.loadTitle()) { it.title }
//    }

    /**
     * Refresh the current title and save the results to the offline cache.
     *
     * This method does not return the new title. Use [TitleRepository.title] to observe
     * the current tile.
     */
    suspend fun sendEmail(email: String) {
        val url = URL("${TOTP_API}$email")
        val urlConnection = url.openConnection() as HttpsURLConnection
        var result = null

        try {
            val inputStream = BufferedInputStream(urlConnection.getInputStream())
            val buffer = ByteArrayOutputStream()
            var resultStream = inputStream.read()
            while (resultStream != -1) {
                buffer.write(resultStream.toByte().toInt())
                resultStream = inputStream.read()
            }
            ULog.d(TAG, "response: ${buffer.toString("UTF-8")}")

            inputStream.close()
        } catch (exception: Exception) {

        } finally {
            urlConnection.disconnect()
        }
    }

    companion object {

        private const val TAG = "QRCodeRepository"

        private const val TOTP_API = "https://uat-shopping56.udn.com/spm/TOTPGenQrcodeMail.do?source="

    }
}
