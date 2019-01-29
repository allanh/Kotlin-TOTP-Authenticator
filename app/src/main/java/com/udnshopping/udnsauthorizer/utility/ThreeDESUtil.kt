package com.udnshopping.udnsauthorizer.utility

import android.util.Base64
import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec

object ThreeDESUtil {

    val DES_FLAG = "DESede"                     // 3DES
    val ENCODE_MODE = Cipher.ENCRYPT_MODE       // 加密模式
    val DECODE_MODE = Cipher.DECRYPT_MODE       // 解密模式

    val PASSWORD = "lyAOvVCxkYvyTzSeEyRwkfzX"

    private fun desEncypt(mode: Int, password: String, content: ByteArray): ByteArray {

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
        val desEncypt = desEncypt(
            ENCODE_MODE,
            password,
            bytes
        )
        val encode = Base64.encode(desEncypt, Base64.DEFAULT)
        return String(encode)
    }

    private fun deCode(password: String, message: String): String {
        val bytes = Base64.decode(message, Base64.DEFAULT)
        val desEncypt = desEncypt(
            DECODE_MODE,
            password,
            bytes
        )
        return String(desEncypt)
    }

    fun encrypt(message: String): String {
        return enCode(
            PASSWORD,
            message
        )
    }

    fun decrypt(message: String): String {
        return deCode(
            PASSWORD,
            message
        )
    }
}