package com.harsh.notes.repository

import com.harsh.notes.db.NotesDao
import com.harsh.notes.db.DeletedNote
import com.harsh.notes.db.Note
import com.harsh.notes.db.toNote
import com.harsh.notes.db.toNoteEntity
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class NotesRepositoryImpl @Inject constructor(private val notesDao: NotesDao) : NotesRepository {

    override
    fun fetchAllNotes(state: Int) = notesDao.fetchAllNotes(state).map { it.map { it.toNoteEntity() } }

    override
    suspend fun fetchNote(noteId: Int) = notesDao.findNoteById(noteId)?.toNoteEntity()

    override
    suspend fun insertNote(note: NoteEntity) = notesDao.insertNote(note.toNote())

    override
    suspend fun insertNotes(notes: List<NoteEntity>) = notesDao.insertNotes(notes.map { it.toNote() })

    override
    suspend fun updateOrInsertNote(note: NoteEntity): Int {
        val flag = notesDao.updateNote(note.toNote())
        if (flag <= 0) {
            return notesDao.insertNote(note.toNote()).toInt()
        } else {
            return flag
        }
    }

    override
    suspend fun deleteNote(noteId: Int): Int {
        // for testing
        val deletingNote = fetchNote(noteId = noteId)?.toNote()
        deletingNote?.let {
            notesDao.insertDeletedNote(DeletedNote.cloneNote(it))
        }
        return notesDao.deleteNote(noteId)
    }

    override suspend fun draftNote(note: NoteEntity): Int {
        return updateOrInsertNote(note)
    }

    override suspend fun changeNoteState(noteId: Int, state: Int): Int {
        return notesDao.changeNoteState(id = noteId, state = state)
    }

    override suspend fun restoreDeletedNote(noteId: Int) {
        notesDao.findDeletedNoteById(noteId)?.let { deleteNote ->
            notesDao.insertNote(Note(id = deleteNote.id, body = deleteNote.body, createdDate = deleteNote.createdDate, updatedDate = deleteNote.date, state = Note.SAVED))
        }
    }
}