package com.notes.shared

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import okio.Path.Companion.toPath

class DataStoreAndroidImpl(private val dataStore: androidx.datastore.core.DataStore<Preferences>) :
    DataStore {

    override suspend fun setData(key: String, value: String) {
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun getData(key: String): String? {
        return dataStore.data.first()[stringPreferencesKey(key)]
    }
}

object DataStoreProvider {
    private const val dataStoreFileName = "app.preferences_pb"

    private lateinit var dataStore: androidx.datastore.core.DataStore<Preferences>

    fun get(context: Context) = if (::dataStore.isInitialized) {
        dataStore
    } else {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { context.filesDir.resolve(dataStoreFileName).absolutePath.toPath() })
        dataStore
    }
}