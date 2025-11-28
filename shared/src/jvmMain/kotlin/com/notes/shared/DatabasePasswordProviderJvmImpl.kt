package com.notes.shared

class DatabasePasswordProviderJvmImpl : DatabasePasswordProvider {

    val map = mutableMapOf<String, String>()

    override suspend fun getPassword(): String? {
        return map.get("pass")
    }

    override suspend fun setPassword(newPassword: String) {
        map["pass"] = newPassword
    }
}