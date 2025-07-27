package com.notes.shared

interface DatabasePasswordProvider {
    suspend fun getPassword(): String?

    suspend fun setPassword(newPassword: String)
}