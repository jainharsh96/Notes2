package com.notes.shared

class DatabasePasswordProviderIOSImpl : DatabasePasswordProvider {

    override suspend fun getPassword(): String? {
        return "Not_required_on_ios"
    }

    override suspend fun setPassword(newPassword: String) {
        // No implementation needed for iOS, as not required at all.
    }
}

