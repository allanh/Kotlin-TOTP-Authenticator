package com.udnshopping.udnsauthorizer.utility

import android.util.Base64
import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec

object ThreeDESUtil {

    private const val DES_FLAG = "DESede"                     // 3DES
    private const val ENCODE_MODE = Cipher.ENCRYPT_MODE       // 加密模式
    private const val DECODE_MODE = Cipher.DECRYPT_MODE       // 解密模式

    init {
        System.loadLibrary("native-lib")
    }

    private fun desEncrypt(mode: Int, password: String, content: ByteArray): ByteArray {

        val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
        var result = ByteArray(16)
        try {
            val factory = SecretKeyFactory.getInstance(DES_FLAG)
            val spec = DESedeKeySpec(password.toByteArray())
            val key = factory.generateSecret(spec)
            cipher.init(mode, key)
            result = cipher.doFinal(content)
        } catch (e: Exception) {
            println(e)
        } finally {
            return result
        }
    }

    private fun enCode(password: String, message: String): String {
        val bytes = message.toByteArray()
        val desEncrypt = desEncrypt(
            ENCODE_MODE,
            password,
            bytes
        )
        val encode = Base64.encode(desEncrypt, Base64.DEFAULT)
        return String(encode)
    }

    private fun deCode(password: String, message: String): String {
        val bytes = Base64.decode(message, Base64.DEFAULT)
        val desEncrypt = desEncrypt(
            DECODE_MODE,
            password,
            bytes
        )
        return String(desEncrypt)
    }

    @Suppress("unused")
    fun encrypt(message: String): String {
        return enCode(
            getPassword(),
            message
        )
    }

    fun decrypt(message: String): String {
        return deCode(
            getPassword(),
            message
        )
    }

    private external fun getPassword(): String
}