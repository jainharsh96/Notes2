package com.notes.shared

interface DatabasePasswordProvider {
    fun getPassword(): String?

    fun setPassword(newPassword: String)
}