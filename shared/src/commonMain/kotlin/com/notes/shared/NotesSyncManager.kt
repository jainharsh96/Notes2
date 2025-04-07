package com.notes.shared

interface NotesSyncManager {

    fun hasSupportSync() : Boolean

    fun syncDataToCloud()

    fun restoreDataFromCloud()
}