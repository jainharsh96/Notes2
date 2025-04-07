package com.harsh.notes

import android.accounts.Account
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.notes.shared.AppDispatcherImpl
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.NotesSyncManager
import com.notes.shared.db.NotesDatabase
import com.notes.shared.getDatabasePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class NotesSyncManagerAndroidImpl(
    private val context: ComponentActivity,
    private val globalScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val dispatcher: AppDispatcherProvider = AppDispatcherImpl()
) : NotesSyncManager {

    companion object {
        const val SERVER_CLIENT_ID = "251167491395-58gvoqtqpu8ft090mkkt9tppokruduji.apps.googleusercontent.com"
    }

    var onLoginSuccess: (Account) -> Unit = {}
    var onFailure: (Exception) -> Unit = {}

    // Launcher for sign-in result
    private val signInLauncher =
        context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val googleAccount = task.getResult(ApiException::class.java)
                onLoginSuccess(googleAccount.account!!)
                onLoginSuccess = {}
            } catch (e: Exception) {
                onFailure(e)
                onFailure = {}
            }
        }

    override fun hasSupportSync(): Boolean {
        return true
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

    override fun syncDataToCloud() {
        googleSignIn(
            onLoginSuccess = { account ->
                globalScope.launch {
                    uploadToDrive(account = account).onSuccess {
                        showToast("successfully uploaded file")
                    }.onFailure {
                        showToast("Something went wrong while uploading data")
                    }
                }
            },
            onFailure = {
                showToast("Something went wrong while uploading data")
            }
        )
    }

    override fun restoreDataFromCloud() {
        googleSignIn(
            onLoginSuccess = { account ->
                globalScope.launch {
                    syncFromDrive(account = account).onSuccess {
                        showToast("successfully downloaded")
                        runCatching { NotesDatabase.reInitDatabase() }.getOrNull()
                    }.onFailure {
                        showToast("Something went wrong while restoring data")
                    }
                }
            },
            onFailure = {
                showToast("Something went wrong while restoring data")
            }
        )
    }

    private fun googleSignIn(onLoginSuccess: (Account) -> Unit, onFailure: (Exception) -> Unit) {
        val lastSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (lastSignInAccount?.account != null){
           onLoginSuccess(lastSignInAccount.account!!)
        } else {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_CLIENT_ID)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE)) // Access to app-specific files
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            this.onLoginSuccess = onLoginSuccess
            this.onFailure = onFailure
            signInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private suspend fun uploadToDrive(account: Account) = kotlin.runCatching {
        withContext(dispatcher.IO) {

            val driveService = getDriveService(account)
            val filePath = getDatabaseFile()
            val mediaContent = FileContent(null, filePath)
            val query = "name = '${NotesDatabase.DATABASE_FILE_NAME_V2}' and trashed = false"

            // check whether file already present or not
            val fileList = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()

            if (fileList.files.isNullOrEmpty()){
                // create file
                val fileMetadata = File().apply {
                    name = NotesDatabase.DATABASE_FILE_NAME_V2
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
        }
    }

    private fun getDatabaseFile(): java.io.File? {
        return try {
            java.io.File(getDatabasePath())
        } catch (e: Exception) {
            Log.e("harshtag", "error in getting file $e")
            null
        }
    }

    private suspend fun syncFromDrive(account: Account) = kotlin.runCatching {
        withContext(dispatcher.IO) {
            val driveService = getDriveService(account)

            // List files
            val query = "name = '${NotesDatabase.DATABASE_FILE_NAME_V2}' and trashed = false"
            val result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()

            result.files.firstOrNull()?.let { file ->
                val outputStream = ByteArrayOutputStream()
                driveService.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                FileOutputStream(getDatabaseFile()).use { fileOutputStream ->
                    outputStream.writeTo(fileOutputStream)
                }
            }
        }
    }

    private fun showToast(msg: String) {
        globalScope.launch {
            withContext(dispatcher.Main){
                Toast.makeText(context.applicationContext, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}