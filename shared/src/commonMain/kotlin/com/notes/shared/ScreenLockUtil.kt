package com.notes.shared

class ScreenLockUtil(private val dataStore: DataStore?) {
    companion object {
        private const val KEY_SCREEN_LOCK = "screen_lock"
    }

    var isScreenUnLocked : Boolean = false
        private set

    suspend fun getUnlockPassword() = dataStore?.getData(KEY_SCREEN_LOCK)

    suspend fun setUnlockPassword(password : String) = dataStore?.setData(KEY_SCREEN_LOCK, password)

    fun setScreenUnlocked() {
        isScreenUnLocked = true
    }
}