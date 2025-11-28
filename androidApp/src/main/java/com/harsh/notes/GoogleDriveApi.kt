package com.harsh.notes

import android.accounts.Account
import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.notes.shared.AppDispatcherImpl
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.db.NotesDatabaseDelegate
import com.notes.shared.getDatabasePath
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class GoogleDriveApi(
    private val context: Context,
    private val dispatcher: AppDispatcherProvider = AppDispatcherImpl()
) {
    private fun getDatabaseFile(): java.io.File? {
        return try {
            java.io.File(getDatabasePath())
        } catch (e: Exception) {
            Log.e("harshtag", "error in getting file $e")
            null
        }
    }

    private fun getDriveService(account: Account) : Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
        ).apply { selectedAccount = account }

        // Build Drive service
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("Notes2").build()
    }

    suspend fun uploadToDrive(account: Account) = kotlin.runCatching {
        withContext(dispatcher.IO) {

            val driveService = getDriveService(account)
            val filePath = getDatabaseFile()
            val mediaContent = FileContent(null, filePath)
            val query = "name = '${NotesDatabaseDelegate.DATABASE_FILE_NAME_V2}' and trashed = false"

            // check whether file already present or not
            val fileList = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()

            if (fileList.files.isNullOrEmpty()) {
                // create file
                val fileMetadata = File().apply {
                    name = NotesDatabaseDelegate.DATABASE_FILE_NAME_V2
                }
                driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
            } else {
                // update file
                val existingFileId = fileList.files[0].id
                driveService.files().update(existingFileId, null, mediaContent)
                    .execute()
            }
            Unit
        }
    }

    suspend fun syncFromDrive(account: Account) = kotlin.runCatching {
        withContext(dispatcher.IO) {
            val driveService = getDriveService(account)

            // List files
            val query = "name = '${NotesDatabaseDelegate.DATABASE_FILE_NAME_V2}' and trashed = false"
            val result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()

            result.files.firstOrNull()?.let { file ->
                val outputStream = ByteArrayOutputStream()
                driveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                // download file
                FileOutputStream(getDatabaseFile()).use { fileOutputStream ->
                    outputStream.writeTo(fileOutputStream)
                }
            }
            Unit
        }
    }
}