package com.notes.shared.domain

import com.notes.shared.NotesDependencies
import com.notes.shared.db.NotesDatabaseDelegate

class NotesDbUseCase {

    fun isDBInitialized() = NotesDatabaseDelegate.isDBAlreadyInitialized()

    suspend fun tryInitDb() = NotesDatabaseDelegate.tryInitDb()

    suspend fun isPasswordSetAndCorrect() : Boolean {
        return NotesDependencies.databasePasswordProvider?.getPassword()?.isNotEmpty() == true && tryInitDb()
    }

    suspend fun setPassword(newPassword: String) {
        NotesDependencies.databasePasswordProvider?.setPassword(newPassword)
    }
}