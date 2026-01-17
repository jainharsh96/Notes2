package com.harsh.notes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notes.shared.db.NotesDatabaseDelegate
import com.notes.shared.db.toReminder
import com.notes.shared.db.toReminderEntity
import com.notes.shared.repository.ReminderRepository
import com.notes.shared.ui.uientity.ReminderState
import com.notes.shared.utils.DateFormatter
import com.notes.shared.utils.NotesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

class NotesBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACKNOWLEDGE_REMINDER = "ACKNOWLEDGE_REMINDER"
        const val REMINDER_ID = "reminder_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACKNOWLEDGE_REMINDER -> {
                val reminderId = intent.getIntExtra(REMINDER_ID, -1)
                if (reminderId != -1) {
                    acknowledgeReminder(reminderId)
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(reminderId)
                }
            }
        }
    }

    fun acknowledgeReminder(reminderId: Int) {
        val globalScope: CoroutineScope = getKoin().get()
        globalScope.launch {
            NotesDatabaseDelegate.tryInitDb()
            val reminderRepo: ReminderRepository = getKoin().get()
            var reminder = reminderRepo.fetchReminder(reminderId)?.toReminder()
            if (reminder != null) {
                reminder = if (reminder.frequency > 0) {
                    val newDateLong = DateFormatter.addDays(
                        dateTimeInMillis = reminder.remindAt,
                        daysToAdd = reminder.frequency
                    )
                    reminder.copy(remindAt = newDateLong)
                } else {
                    reminder.copy(state = ReminderState.COMPLETED.value)
                }
                reminderRepo.updateOrInsertReminder(reminder.toReminderEntity())
            } else {
                NotesLogger.log("NotesBroadcastReceiver", "No reminder found with id $reminderId")
            }
        }
    }
}