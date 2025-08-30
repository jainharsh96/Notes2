package com.notes.shared.repository

import com.notes.shared.AppDispatcherProvider
import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.ReminderDao
import com.notes.shared.db.toReminder
import com.notes.shared.db.toReminderEntity
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.ui.uientity.ReminderState
import com.notes.shared.utils.DateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ReminderRepository {

    fun fetchAllReminders(states: List<Int> = ReminderState.getIncompletedStates()): Flow<List<ReminderEntity>>

    fun fetchNoteReminders(
        noteId: Int,
        states: List<Int> = ReminderState.getIncompletedStates()
    ): Flow<List<ReminderEntity>>

    suspend fun getAllActiveRemindersToNotify(currentDateTime: Long = DateFormatter.currentDateTimeMillisecond()): List<ReminderEntity>

    suspend fun fetchReminder(reminderId: Int): ReminderEntity?

    suspend fun insertReminder(reminder: ReminderEntity): Long

    suspend fun updateOrInsertReminder(reminder: ReminderEntity): Int

    suspend fun deleteReminder(reminderId: Int): Int
}

class ReminderRepositoryImpl(
    private val globalScope: CoroutineScope,
    private val dispatcherProvider: AppDispatcherProvider,
) : ReminderRepository {

    private val reminderDao: ReminderDao
        get() = NotesDatabase.databaseObj!!.reminderDao()

    override fun fetchAllReminders(states: List<Int>) =
        reminderDao.fetchAllReminders(states).map { it.map { it.toReminderEntity() } }

    override fun fetchNoteReminders(noteId: Int, states: List<Int>) =
        reminderDao.fetchNoteReminders(noteId, states).map { it.map { it.toReminderEntity() } }

    override suspend fun getAllActiveRemindersToNotify(currentDateTime: Long): List<ReminderEntity> {
        return reminderDao.getAllActiveReminders(ReminderState.SET.value, currentDateTime)
            .map { it.toReminderEntity() }
    }

    override suspend fun fetchReminder(reminderId: Int) =
        reminderDao.findReminderById(reminderId)?.toReminderEntity()

    override suspend fun insertReminder(reminder: ReminderEntity) =
        globalScope.async(dispatcherProvider.IO) {
            reminderDao.insertReminder(reminder.toReminder())
        }.await()

    override suspend fun updateOrInsertReminder(reminder: ReminderEntity): Int {
        return globalScope.async(dispatcherProvider.IO) {
            val flag = reminderDao.updateReminder(reminder.toReminder())
            if (flag <= 0) {
                reminderDao.insertReminder(reminder.toReminder()).toInt()
            } else {
                flag
            }
        }.await()
    }

    override suspend fun deleteReminder(reminderId: Int): Int {
        // todo
        return -1
    }

}