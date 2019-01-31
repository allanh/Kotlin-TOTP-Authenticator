package com.udnshopping.udnsauthorizer.utility

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

object UdnSSLContextFactory {

    private const val TAG = "UdnSSLContextFactory"

    /**
     * Creates a new SSLContext instance, loading the CA from the [input] stream.
     */
    fun getSSLContext(input: InputStream): SSLContext? {
        try {
            val ca = loadCertificate(input)
            val keyStore = createKeyStore(ca)
            val trustManagers = createTrustManager(keyStore)
            return createSSLContext(trustManagers)
        } catch (e: CertificateException) {
            Logger.e(TAG, "Failed to create certificate factory, ${e.message}")
        } catch (e: KeyStoreException) {
            Logger.e(TAG, "Failed to get key store instance, ${e.message}")
        } catch (e: KeyManagementException) {
            Logger.e(TAG, "Failed to initialize SSL Context, ${e.message}")
        }
        return null
    }

    /**
     * Loads CAs from an [inputStream]. Could be from a resource.
     */
    @Throws(CertificateException::class)
    private fun loadCertificate(inputStream: InputStream): X509Certificate {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = BufferedInputStream(inputStream)
        return caInput.use {
            cf.generateCertificate(it) as X509Certificate
        }
    }

    /**
     * Creates a key store using the [certificate].
     */
    @Throws(KeyStoreException::class)
    private fun createKeyStore(certificate: Certificate): KeyStore? {
        try {
            val keyStoreType = KeyStore.getDefaultType()
            return KeyStore.getInstance(keyStoreType).apply {
                load(null, null)
                setCertificateEntry("ca", certificate)
            }
        } catch (e: IOException) {
            Logger.e(TAG, "Could not load key store, ${e.message}")
        } catch (e: NoSuchAlgorithmException) {
            Logger.e(TAG, "Could not load key store, ${e.message}")
        } catch (e: CertificateException) {
            Logger.e(TAG, "Could not load key store, ${e.message}")
        }
        return null
    }

    /**
     * Creates a TrustManager that trusts the CAs in our [keyStore].
     */
    @Throws(KeyStoreException::class)
    private fun createTrustManager(keyStore: KeyStore?): Array<TrustManager>? {
        try {
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
                init(keyStore)
            }
            return tmf.trustManagers
        } catch (e: NoSuchAlgorithmException) {
            Logger.e(TAG, "Failed to get trust manager factory with default algorithm, ${e.message}")
        }
        return null
    }

    /**
     * Creates an SSL Context that uses a specific [trustManagers].
     */
    @Throws(KeyManagementException::class)
    private fun createSSLContext(trustManagers: Array<TrustManager>?): SSLContext? {
        try {
            return SSLContext.getInstance("TLS").apply {
                init(null, trustManagers, null)
            }
        } catch (e: NoSuchAlgorithmException) {
            Logger.e(TAG, "Failed to initialize SSL context with TLS algorithm, ${e.message}")
        }
        return null
    }
}