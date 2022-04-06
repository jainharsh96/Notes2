package com.harsh.notes.repository

import com.harsh.notes.db.NotesDao
import com.harsh.notes.models.DeletedNote
import com.harsh.notes.models.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface NotesRepository {
    fun fetchAllNotes(state: Int): Flow<List<Note>>
    suspend fun fetchNote(noteId: Int): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun insertNotes(notes: List<Note>): List<Long>?
    suspend fun updateOrInsertNote(note: Note): Int
    suspend fun deleteNote(noteId: Int): Int
    suspend fun draftNote(note: Note): Int
    suspend fun changeNoteState(noteId: Int, state: Int): Int
}

@Singleton
class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) : NotesRepository {

    override
    fun fetchAllNotes(state: Int) = notesDao.fetchAllNotes(state)

    override
    suspend fun fetchNote(noteId: Int) = notesDao.findNoteById(noteId)

    override
    suspend fun insertNote(note: Note) = notesDao.insertNote(note)

    override
    suspend fun insertNotes(notes: List<Note>) = notesDao.insertNotes(notes)

    override
    suspend fun updateOrInsertNote(note: Note): Int {
        val flag = notesDao.updateNote(note)
        if (flag <= 0) {
            return notesDao.insertNote(note).toInt()
        } else {
            return flag
        }
    }

    override
    suspend fun deleteNote(noteId: Int): Int {
        // for testing
        val deletingNote = fetchNote(noteId = noteId)
        deletingNote?.let {
            notesDao.insertDeletedNote(DeletedNote.cloneNote(it))
        }
        return notesDao.deleteNote(noteId)
    }

    override suspend fun draftNote(note: Note): Int {
        return updateOrInsertNote(note)
    }

    override suspend fun changeNoteState(noteId: Int, state: Int): Int {
        return notesDao.changeNoteState(id = noteId, state = state)
    }
}