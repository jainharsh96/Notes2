package com.notes.shared

interface DataStore {
    fun setData(key : String, value : String)

    fun getData(key: String) : String?
}