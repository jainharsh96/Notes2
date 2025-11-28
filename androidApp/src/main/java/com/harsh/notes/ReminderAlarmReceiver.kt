package com.harsh.notes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.notes.shared.db.NotesDatabaseDelegate
import com.notes.shared.utils.DateFormatter
import com.notes.shared.utils.NotesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val globalScope : CoroutineScope = getKoin().get()
        globalScope.launch {
            try {
                val state = NotesDatabaseDelegate.tryInitDb()
                val reminderTriggerTask = ReminderTriggerTask(
                    context = context,
                    reminderRepository = getKoin().get()
                )
                reminderTriggerTask.triggerReminders()
            } catch (e : Exception){
                NotesLogger.log(TAG, "Error in triggering reminder notif ${e.toString()}")
            }

            // Schedule next alarm after interval
            scheduleRepeatingAlarm(context)
        }
    }

    companion object {

        private const val TAG = "ReminderAlarmReceiver"
        private const val ALARM_PREF = "reminder_alarm_pref"
        private const val DEFAULT_INTERVAL = 2 * 60 * 60 * 1000L // 2 hours in milliseconds
        private const val ALARM_REQUEST_CODE = 4321

        fun scheduleRepeatingAlarm(context: Context) {
            val prefs = context.getSharedPreferences(ALARM_PREF, Context.MODE_PRIVATE)

            if (prefs.getBoolean(ALARM_PREF, false)) {
                return
            }

            val triggerAtMillis = DateFormatter.currentDateTimeMillisecond() + DEFAULT_INTERVAL
            val intent = Intent(context, ReminderAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                prefs.edit { putBoolean(ALARM_PREF, true) }
            } catch (e : SecurityException){
                NotesLogger.log(TAG, "Error in scheduling alarm ${e.toString()}")
            }
        }
    }
}