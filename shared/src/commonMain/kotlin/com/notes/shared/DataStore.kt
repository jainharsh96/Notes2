package com.notes.shared

interface DataStore {
    suspend fun setData(key : String, value : String)

    suspend fun getData(key: String) : String?
}