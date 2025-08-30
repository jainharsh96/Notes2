package com.notes.shared.repository

import com.notes.shared.AppDispatcherProvider
import com.notes.shared.db.DeletedNote
import com.notes.shared.db.Note
import com.notes.shared.db.NotesDao
import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.toNote
import com.notes.shared.db.toNoteEntity
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NotesRepository {
    fun fetchAllNotes(state: Int): Flow<List<NoteEntity>>
    suspend fun fetchNote(noteId: Int): NoteEntity?
    suspend fun insertNote(note: NoteEntity): Long
    suspend fun insertNotes(notes: List<NoteEntity>): List<Long>?
    suspend fun updateOrInsertNote(note: NoteEntity): Int
    suspend fun deleteNote(noteId: Int): Int
    suspend fun draftNote(note: NoteEntity): Int
    suspend fun changeNoteState(noteId: Int, state: Int): Int

    // for testing
    suspend fun restoreDeletedNote(noteId: Int)
}

class NotesRepositoryImpl (
    private val globalScope: CoroutineScope,
    private val dispatcherProvider: AppDispatcherProvider,
) : NotesRepository {

    private val notesDao: NotesDao
        get() = NotesDatabase.databaseObj!!.notesDao()

    override
    fun fetchAllNotes(state: Int) =
        notesDao.fetchAllNotes(state).map { it.map { it.toNoteEntity() } }

    override
    suspend fun fetchNote(noteId: Int) = notesDao.findNoteById(noteId)?.toNoteEntity()

    override
    suspend fun insertNote(note: NoteEntity) = globalScope.async(dispatcherProvider.IO) {
        notesDao.insertNote(note.toNote())
    }.await()

    override
    suspend fun insertNotes(notes: List<NoteEntity>) = globalScope.async(dispatcherProvider.IO) {
        notesDao.insertNotes(notes.map { it.toNote() })
    }.await()

    override
    suspend fun updateOrInsertNote(note: NoteEntity): Int {
        return globalScope.async(dispatcherProvider.IO) {
            val flag = notesDao.updateNote(note.toNote())
            if (flag <= 0) {
                notesDao.insertNote(note.toNote()).toInt()
            } else {
                flag
            }
        }.await()
    }

    override
    suspend fun deleteNote(noteId: Int): Int {
        return globalScope.async(dispatcherProvider.IO) {
            // for testing
            val deletingNote = fetchNote(noteId = noteId)?.toNote()
            deletingNote?.let {
                notesDao.insertDeletedNote(DeletedNote.cloneNote(it))
            }
            notesDao.deleteNote(noteId)
        }.await()
    }

    override suspend fun draftNote(note: NoteEntity): Int {
        return updateOrInsertNote(note)
    }

    override suspend fun changeNoteState(noteId: Int, state: Int): Int {
        return notesDao.changeNoteState(id = noteId, state = state)
    }

    override suspend fun restoreDeletedNote(noteId: Int) {
        notesDao.findDeletedNoteById(noteId)?.let { deleteNote ->
            notesDao.insertNote(
                Note(
                    id = deleteNote.id,
                    body = deleteNote.body,
                    createdDate = deleteNote.createdDate,
                    updatedDate = deleteNote.date,
                    state = Note.SAVED
                )
            )
        }
    }
}