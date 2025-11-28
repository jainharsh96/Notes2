package com.notes.shared

class DataStoreJvmImpl : DataStore {
    val map = mutableMapOf<String, String>()

    override suspend fun setData(key: String, value: String) {
        map[key] = value
    }

    override suspend fun getData(key: String): String? {
        return map.get(key)
    }

}