package com.harsh.notes

import android.accounts.Account
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.notes.shared.NotesSyncManager
import com.notes.shared.Result
import com.notes.shared.UserNotLoggedInException
import com.notes.shared.db.NotesDatabase
import com.notes.shared.utils.sendAndClose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

class NotesSyncManagerAndroidImpl(
    private val context: ComponentActivity,
    private val googleCachedAccount: GoogleCachedAccountProvider,
    private val googleDriveApi: GoogleDriveApi,
    private val globalScope: CoroutineScope = getKoin().get()
) : NotesSyncManager {

    companion object {
        const val SERVER_CLIENT_ID = "251167491395-sp2poe8gn4vuknqe50ldbh5pnl2ikoq7.apps.googleusercontent.com"
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

    override suspend fun syncDataToCloud(isBgSync : Boolean) = callbackFlow {
        googleSignIn(
            isBgSignIn = isBgSync,
            onLoginSuccess = { account ->
                globalScope.launch {
                    googleDriveApi.uploadToDrive(account = account).onSuccess {
                        sendAndClose(Result.Success("successfully uploaded file"))
                    }.onFailure {
                        sendAndClose(Result.Error("Something went wrong while uploading data $it"))
                    }
                }
            },
            onFailure = {
                globalScope.launch {
                    sendAndClose(Result.Exception(it))
                }
            }
        )
        awaitClose { close() }
    }.catch { null }.firstOrNull() ?: Result.Error("Something went wrong")

    override suspend fun restoreDataFromCloud() = callbackFlow {
        googleSignIn(
            isBgSignIn = false,
            onLoginSuccess = { account ->
                globalScope.launch {
                    googleDriveApi.syncFromDrive(account = account).onSuccess {
                        runCatching { NotesDatabase.reInitDatabase() }.getOrNull()
                        sendAndClose(Result.Success("successfully downloaded"))
                    }.onFailure {
                        sendAndClose(Result.Error("Something went wrong while restoring data $it"))
                    }
                }
            },
            onFailure = {
                globalScope.launch {
                    sendAndClose(Result.Exception(it))
                }
            }
        )
        awaitClose { close() }
    }.catch { null }.firstOrNull() ?: Result.Error("Something went wrong")

    private suspend fun googleSignIn(isBgSignIn : Boolean, onLoginSuccess: (Account) -> Unit, onFailure: (Exception) -> Unit) {
        val lastSignInAccount = googleCachedAccount.getLastSignedInAccount()
        if (lastSignInAccount != null){
           onLoginSuccess(lastSignInAccount)
        } else {
            if (isBgSignIn){ // can not login in background
                onFailure(UserNotLoggedInException)
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
    }
}