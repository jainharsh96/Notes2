package com.notes.shared.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder where state IN ( :states) ORDER BY remindAt ASC")
    fun fetchAllReminders(states: List<Int>): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder where linkedNoteId = :noteId and state IN ( :states) ORDER BY remindAt ASC")
    fun fetchNoteReminders(noteId: Int, states: List<Int>): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE state = :activeState AND remindAt <= :time")
    suspend fun getAllActiveReminders(activeState : Int, time : Long): List<Reminder>

    @Query("SELECT * FROM reminder WHERE id = :id")
    suspend fun findReminderById(id: Int): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReminder(reminder: Reminder): Int

    @Query("delete from reminder where id = :reminderId")
    suspend fun deleteReminder(reminderId: Int): Int
}