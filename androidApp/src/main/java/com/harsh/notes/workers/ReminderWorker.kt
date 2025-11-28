package com.harsh.notes.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.harsh.notes.ReminderTriggerTask
import com.notes.shared.db.NotesDatabaseDelegate
import com.notes.shared.utils.NotesLogger
import org.koin.mp.KoinPlatform.getKoin
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "ReminderWorker"

        fun setReminderWorker(context: Context) {
            val dailyWork = PeriodicWorkRequestBuilder<ReminderWorker>(
                1, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .build()
            ).build()

            WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }

        // todo testing only
        fun testOnTimeWorker(context: Context) {
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
               // .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        try {
            NotesDatabaseDelegate.tryInitDb()
            val reminderTriggerTask = ReminderTriggerTask(
                context = applicationContext,
                reminderRepository = getKoin().get()
            )
            reminderTriggerTask.triggerReminders()
        } catch (e : Exception){
            NotesLogger.log(WORK_NAME, " Error in triggering reminder ${e.toString()}")
        }
        return Result.success()
    }
}