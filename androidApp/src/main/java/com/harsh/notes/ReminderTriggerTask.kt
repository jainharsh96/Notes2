package com.harsh.notes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.notes.shared.repository.ReminderRepository
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.utils.NotesLogger
import kotlinx.coroutines.delay

class ReminderTriggerTask(
    private val context: Context,
    private val reminderRepository: ReminderRepository,
) {

    companion object Companion {
        private const val NOTIFICATION_CHANNEL_NAME = "Reminders"
    }

    suspend fun triggerReminders() {
        val reminders = reminderRepository.getAllActiveRemindersToNotify()
        reminders.forEach {
            delay(500) // slight delay to avoid notification clash
            triggerNotification(it)
        }
    }

    private fun triggerNotification(reminder: ReminderEntity) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true) // enable vibration
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                // pattern: wait 0ms, vibrate 500ms, pause 200ms, vibrate 500ms
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(reminder.firstLineData())
            .setSmallIcon(R.drawable.app_icon)
            .setContentText(reminder.secondLineData())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .addAction(
                R.drawable.app_icon,
                "Acknowledge",
                getAcknowledgeAction(reminder.id)
            )
            .build()

        notificationManager.notify(reminder.id, notification)
    }

    private fun getAcknowledgeAction(reminderId : Int): PendingIntent {
        val intent = Intent(context, NotesBroadcastReceiver::class.java).apply {
            action = NotesBroadcastReceiver.ACKNOWLEDGE_REMINDER
            putExtra(NotesBroadcastReceiver.REMINDER_ID, reminderId)
        }
        return PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}