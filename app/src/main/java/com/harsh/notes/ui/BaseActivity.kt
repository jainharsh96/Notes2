package com.harsh.notes.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.harsh.notes.utils.REQUEST_CODE_APP_PERMISSION
import com.example.harsh.Notes.NoteUtils.getNotesAllPermissions
import com.harsh.notes.BuildConfig

open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        if (!checkPermissions(getNotesAllPermissions())) {
            requestPermissions(getNotesAllPermissions(), REQUEST_CODE_APP_PERMISSION)
            Process.killProcess(Process.myPid())
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_APP_PERMISSION) {
            for (i in getNotesAllPermissions().indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                    finish()
                }
            }
        }
    }
}