package com.notes.shared

import android.content.Context

class DataStoreAndroidImpl(private val context: Context) : DataStore {
    override fun setData(key: String, value: String) {
        AndroidKeystoreUtil.setData(context = context, key = key, value = value)
    }

    override fun getData(key: String) : String?{
        return AndroidKeystoreUtil.getData(context = context, key = key)
    }
}