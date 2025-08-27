package com.notes.shared.repository

import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.toReminder
import com.notes.shared.db.toReminderEntity
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.ui.uientity.ReminderState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ReminderRepository {

    fun fetchAllReminders(states: List<Int> = ReminderState.getIncompletedStates()): Flow<List<ReminderEntity>>

    fun fetchNoteReminders(
        noteId: Int,
        states: List<Int> = ReminderState.getIncompletedStates()
    ): Flow<List<ReminderEntity>>

    suspend fun fetchReminder(reminderId: Int): ReminderEntity?

    suspend fun insertReminder(reminder: ReminderEntity): Long

    suspend fun updateOrInsertReminder(reminder: ReminderEntity): Int

    suspend fun deleteReminder(reminderId: Int): Int

}

class ReminderRepositoryImpl() : ReminderRepository {

    private val reminderDao = NotesDatabase.databaseObj!!.reminderDao()

    override fun fetchAllReminders(states: List<Int>) =
        reminderDao.fetchAllReminders(states).map { it.map { it.toReminderEntity() } }

    override fun fetchNoteReminders(noteId: Int, states: List<Int>) =
        reminderDao.fetchNoteReminders(noteId, states).map { it.map { it.toReminderEntity() } }

    override suspend fun fetchReminder(reminderId: Int) =
        reminderDao.findReminderById(reminderId)?.toReminderEntity()

    override suspend fun insertReminder(reminder: ReminderEntity) =
        reminderDao.insertReminder(reminder.toReminder())

    override suspend fun updateOrInsertReminder(reminder: ReminderEntity): Int {
        val flag = reminderDao.updateReminder(reminder.toReminder())
        if (flag <= 0) {
            return reminderDao.insertReminder(reminder.toReminder()).toInt()
        } else {
            return flag
        }
    }

    override suspend fun deleteReminder(reminderId: Int): Int {
        // todo
        return -1
    }

}