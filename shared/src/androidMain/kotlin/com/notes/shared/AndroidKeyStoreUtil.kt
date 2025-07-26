package com.notes.shared

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import android.util.Base64

object AndroidKeystoreUtil {
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "Notes-app-pass-alias"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"

    // Generate or get the secret key from Keystore
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeySize(256)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }

        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    // Encrypt a password and return the encrypted data with IV
    private fun encryptData(password: String): Pair<String, String>? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val iv = cipher.iv // Initialization Vector
            val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
            Pair(
                Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
                Base64.encodeToString(iv, Base64.DEFAULT)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Decrypt a password using the encrypted data and IV
    private fun decryptData(encryptedPassword: String, iv: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), ivSpec)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedPassword, Base64.DEFAULT))
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Save encrypted password to SharedPreferences
    fun savePassword(context: Context, password: String) {
        val (encryptedPassword, iv) = encryptData(password) ?: return
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit()
            .putString("encrypted_password", encryptedPassword)
            .putString("iv", iv)
            .apply()
    }

    // Retrieve and decrypt password from SharedPreferences
    fun getPassword(context: Context): String? {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val encryptedPassword = prefs.getString("encrypted_password", null)
        val iv = prefs.getString("iv", null)
        return if (encryptedPassword != null && iv != null) {
            decryptData(encryptedPassword, iv)
        } else {
            null
        }
    }

    fun setData(context: Context, key : String, value: String) {
        context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit()
            .putString(key, value)
           // .putString("iv", iv)
            .apply()
    }

    fun getData(context: Context, key : String): String? {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        return prefs.getString(key, null)
    }
}