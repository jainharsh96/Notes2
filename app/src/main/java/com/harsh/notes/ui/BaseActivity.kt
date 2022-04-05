package com.harsh.notes.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.harsh.Notes.NoteUtils.REQUEST_CODE_APP_PERMISSION
import com.example.harsh.Notes.NoteUtils.RESULT_SPEECH
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

    fun checkPermissionForAudio() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 99)
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

    fun startRecognizeVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US")
        try {
            startActivityForResult(intent, RESULT_SPEECH)
            Log.e("harshtag", "show voice")
        } catch (a: ActivityNotFoundException) {
            Log.e("harshtag", a.stackTraceToString())
            showToast("Download Voice Search App")
        }
    }

    open fun onRecognizeVoiceText(texts: ArrayList<String?>?) {

    }


    fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(applicationContext, msg, duration).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                val text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                onRecognizeVoiceText(text)
            }
        }
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

    open fun hideKeyboard(activity: Activity = this) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}