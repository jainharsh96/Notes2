package com.notes.shared.domain

import com.notes.shared.NotesDependencies
import com.notes.shared.db.NotesDatabaseDelegate

class NotesDbUseCase {

    private suspend fun getDBPasscode() =
        NotesDependencies.databasePasswordProvider?.getPassword()

    suspend fun tryInitDb() = NotesDatabaseDelegate.tryToOpenDatabase()

    suspend fun saveDbPassword(newPassword: String) {
        NotesDependencies.databasePasswordProvider?.setPassword(newPassword)
    }

    suspend fun tryOpeningDbWithPassword(password: String) : Boolean {
        return NotesDatabaseDelegate.openDataBase(password)
    }

    suspend fun setupNewDatabase(password: String) : Boolean {
        return NotesDatabaseDelegate.setupNewDataBase(password)
    }

    suspend fun getAppDatabaseState() : AppDatabaseState {
        return if (NotesDatabaseDelegate.getDatabase() != null) {
            AppDatabaseState.INITIALIZED
        } else {
            if (NotesDatabaseDelegate.isDatabaseFilePresent().not()){
                AppDatabaseState.DATABASE_FILE_NOT_PRESENT
            } else if (getDBPasscode().isNullOrEmpty()){
                AppDatabaseState.PASSWORD_NOT_PRESENT
            } else {
                AppDatabaseState.UNINITIALIZED
            }
        }
    }
}

enum class AppDatabaseState {
    INITIALIZED,
    DATABASE_FILE_NOT_PRESENT,
    PASSWORD_NOT_PRESENT,
    UNINITIALIZED,
}

fun AppDatabaseState.isDatabaseReady() : Boolean {
    return this == AppDatabaseState.INITIALIZED
}