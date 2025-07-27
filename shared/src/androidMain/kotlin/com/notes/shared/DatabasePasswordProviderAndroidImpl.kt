package com.notes.shared

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class DatabasePasswordProviderAndroidImpl(private val dataStore: DataStore<Preferences>) :
    DatabasePasswordProvider {
    companion object {
        private const val PASSWORD_KEY = "pass_key"
        private const val IV_SUFFIX = "_iv"
    }

    override suspend fun getPassword(): String? {
        val dataPreference = dataStore.data.first()
        val encryptedPassword = dataPreference[stringPreferencesKey(PASSWORD_KEY)] ?: return null
        val iv = dataPreference[stringPreferencesKey(PASSWORD_KEY + IV_SUFFIX)] ?: return null
        return AndroidSecureDataUtil.decryptData(encryptedData = encryptedPassword, iv = iv)
    }

    override suspend fun setPassword(newPassword: String) {
        val (encryptedPassword, iv) = AndroidSecureDataUtil.encryptData(data = newPassword)
            ?: return
        dataStore.edit { settings ->
            settings[stringPreferencesKey(PASSWORD_KEY)] = encryptedPassword
            settings[stringPreferencesKey(PASSWORD_KEY + IV_SUFFIX)] = iv
        }
    }
}