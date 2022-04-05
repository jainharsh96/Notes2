package com.harsh.notes.db

import androidx.room.*
import com.harsh.notes.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes where state = :state ORDER BY updated_date DESC")
    fun fetchAllNotes(state: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>): List<Long>?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note): Int

    @Query("delete from notes where id = :noteId")
    suspend fun deleteNote(noteId: Int): Int

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun findNoteById(id: Int): Note?

    @Query("update Notes set state = :state where id = :id")
    suspend fun changeNoteState(id: Int, state: Int): Int
}