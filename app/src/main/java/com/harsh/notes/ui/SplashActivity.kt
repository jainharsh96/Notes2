package com.harsh.notes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.harsh.notes.utils.REQUEST_CODE_APP_PERMISSION
import com.example.harsh.Notes.NoteUtils.getNotesAllPermissions
import com.harsh.notes.BuildConfig
import com.harsh.notes.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG = SplashActivity::class.java.simpleName
    }

    private val permissionList by lazy { getNotesAllPermissions() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)
        if (checkPermissions(permissionList)) {
            validateBiometric()
        } else {
            requestPermissions(permissionList, REQUEST_CODE_APP_PERMISSION)
        }
    }

    private fun openNotesActivity() {
        startActivity(NotesActivity.getIntent(this))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_APP_PERMISSION) {
            for (i in permissionList.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                    finish()
                    return
                }
            }
            validateBiometric()
        }
    }

    private fun validateBiometric() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this,
            executor, object : BiometricPrompt.AuthenticationCallback() {
                override
                fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    Toast.makeText(
                        applicationContext, errString,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                override
                fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    openNotesActivity()
                }

                override
                fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Notes")
            .setDescription("Tap on Sensor")
            //.setNegativeButtonText("Enter Pin")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun checkPermissions(permissionList: Array<String>): Boolean {
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }
}