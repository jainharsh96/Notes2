package com.harsh.notes

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.notes.shared.AppDispatcherImpl
import com.notes.shared.AppDispatcherProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class GoogleCachedAccountProvider(
    private val context: Context,
    private val dispatcher: AppDispatcherProvider = AppDispatcherImpl()
) {

    suspend fun getLastSignedInAccount(): Account? = withContext(dispatcher.IO) {
        return@withContext GoogleSignIn.getLastSignedInAccount(context)?.account
            ?: silentSignIn(context)?.account
    }

    private suspend fun silentSignIn(context: Context): GoogleSignInAccount? =
        suspendCancellableCoroutine { cont ->
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()

            val client = GoogleSignIn.getClient(context, gso)

            client.silentSignIn()
                .addOnSuccessListener { account -> cont.resume(account) {} }
                .addOnFailureListener { cont.resume(null) {} }
        }
}