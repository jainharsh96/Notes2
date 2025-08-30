package com.harsh.notes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.toReminder
import com.notes.shared.db.toReminderEntity
import com.notes.shared.repository.ReminderRepositoryImpl
import com.notes.shared.ui.uientity.ReminderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACKNOWLEDGE_REMINDER = "ACKNOWLEDGE_REMINDER"
        const val REMINDER_ID = "reminder_id"
    }
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACKNOWLEDGE_REMINDER -> {
                val reminderId = intent.getIntExtra(REMINDER_ID, -1)
                if (reminderId != -1){
                    acknowledgeReminder(reminderId)
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(reminderId)
                }
            }
        }
    }

    fun acknowledgeReminder(reminderId : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            NotesDatabase.tryInitDb()
            val reminderRepo = ReminderRepositoryImpl()  // todo use DI and global scope
            var reminder = reminderRepo.fetchReminder(reminderId)?.toReminder()
            if (reminder != null) {
                reminder = if (reminder.frequency > 0){
                    val newDateLong = reminder.remindAt + reminder.frequency * 24 * 60 * 60 * 1000
                    reminder.copy(remindAt = newDateLong)
                } else {
                    reminder.copy(state = ReminderState.COMPLETED.value)
                }
                reminderRepo.updateOrInsertReminder(reminder.toReminderEntity())
            }
        }
    }
}