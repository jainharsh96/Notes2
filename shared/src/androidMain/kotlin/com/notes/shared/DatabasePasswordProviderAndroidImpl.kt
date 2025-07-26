package com.notes.shared

import android.content.Context

class DatabasePasswordProviderAndroidImpl(private val context: Context) : DatabasePasswordProvider {
    override fun getPassword(): String? {
        return AndroidKeystoreUtil.getPassword(context)
    }

    override fun setPassword(newPassword: String) {
        AndroidKeystoreUtil.savePassword(context = context, password = newPassword)
    }
}