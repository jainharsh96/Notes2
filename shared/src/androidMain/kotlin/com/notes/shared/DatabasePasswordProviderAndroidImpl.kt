package com.notes.shared

import android.content.Context

class DatabasePasswordProviderAndroidImpl(private val context: Context) : DatabasePasswordProvider {
    override fun getPassword(): String? {
        return KeystoreUtil.getPassword(context)
    }

    override fun setPassword(newPassword: String) {
        KeystoreUtil.savePassword(context = context, password = newPassword)
    }
}