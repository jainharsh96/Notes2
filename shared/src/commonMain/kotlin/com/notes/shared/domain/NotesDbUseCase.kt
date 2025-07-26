package com.notes.shared.domain

import com.notes.shared.NotesDependencies
import com.notes.shared.db.NotesDatabase

class NotesDbUseCase {

    fun isDBInitialized() = NotesDatabase.isDBAlreadyInitialized()

    suspend fun tryInitDb() = NotesDatabase.tryInitDb()

    suspend fun isPasswordSetAndCorrect() : Boolean {
        return NotesDependencies.databasePasswordProvider?.getPassword()?.isNotEmpty() == true && tryInitDb()
    }
}