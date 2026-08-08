package com.harsh.notes.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.harsh.notes.GoogleCachedAccountProvider
import com.harsh.notes.GoogleDriveApi
import com.notes.shared.utils.NotesLogger
import java.util.concurrent.TimeUnit

class NoteSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "NoteSyncWorker"

        fun syncNotes(context: Context) {
            val dailyWork = PeriodicWorkRequestBuilder<NoteSyncWorker>(
                1, TimeUnit.DAYS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

            WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }
    }

    private val googleCachedAccount: GoogleCachedAccountProvider =
        GoogleCachedAccountProvider(context)
    private val googleDriveApi: GoogleDriveApi = GoogleDriveApi(context)


    override suspend fun doWork(): Result {
        return try {
            val account = googleCachedAccount.getLastSignedInAccount()
            if (account == null){
                NotesLogger.log(WORK_NAME,"UnSynced Note : no account")
                return Result.failure()
            }
            val result = googleDriveApi.uploadToDrive(account)
            if (result.isSuccess) {
                //NotesLogger.log(WORK_NAME,"Synced Note")
                Result.success()
            } else {
                NotesLogger.log(WORK_NAME,"UnSynced Note with error in upload to drive")
                Result.failure()
            }
        } catch (e: Exception) {
            NotesLogger.log(WORK_NAME,"UnSynced Note with exception ${e.toString()}")
            Result.failure()
        }
    }
}