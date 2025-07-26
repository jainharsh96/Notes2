package com.notes.shared

object ScreenLockUtil {
    const val KEY_SCREEN_LOCK = "screen_lock"

    var isScreenUnLocked : Boolean = false
        private set

    private val dataStore = NotesDependencies.dataStore

    fun getUnlockPassword() = dataStore?.getData(KEY_SCREEN_LOCK)

    fun setUnlockPassword(password : String) = dataStore?.setData(KEY_SCREEN_LOCK, password)

    fun setScreenUnlocked() {
        isScreenUnLocked = true
    }
}