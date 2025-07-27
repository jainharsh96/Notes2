package com.notes.shared

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.first
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

class DataStoreIOSImpl(private val dataStore: androidx.datastore.core.DataStore<Preferences>) :
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

    @OptIn(ExperimentalForeignApi::class)
    val dataStore: androidx.datastore.core.DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                (requireNotNull(documentDirectory).path + "/$dataStoreFileName").toPath()
            }
        )
    }
}