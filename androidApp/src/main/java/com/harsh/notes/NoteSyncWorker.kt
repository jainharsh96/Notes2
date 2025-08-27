package com.harsh.notes

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.notes.shared.db.NotesDatabase
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

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }
    }

    private val googleCachedAccount: GoogleCachedAccountProvider = GoogleCachedAccountProvider(context)
    private val googleDriveApi: GoogleDriveApi = GoogleDriveApi(context)


    override suspend fun doWork(): Result {
        return try {
            val account = googleCachedAccount.getLastSignedInAccount()
            if (account == null){
                makeDbEntry("UnSynced Note : no account")
                return Result.failure()
            }
            val result = googleDriveApi.uploadToDrive(account)
            if (result.isSuccess) {
                makeDbEntry("Synced Note")
                Result.success()
            } else {
                makeDbEntry("UnSynced Note with error")
                Result.failure()
            }
        } catch (e: Exception) {
            makeDbEntry("UnSynced Note with exception ${e.localizedMessage}")
            Result.failure()
        }
    }

    // todo testing only
    private suspend fun makeDbEntry(msg : String) {
        runCatching {
            NotesDatabase.tryInitDb()
            NotesDatabase.databaseObj?.notesDao()?.let { dao ->
                val note = com.notes.shared.db.Note(
                    body = msg,
                    createdDate = System.currentTimeMillis(),
                    updatedDate = System.currentTimeMillis()
                )
                dao.insertNote(note)
            }
        }
    }
}