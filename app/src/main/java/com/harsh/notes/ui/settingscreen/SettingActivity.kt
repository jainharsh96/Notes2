package com.harsh.notes.ui.settingscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.harsh.notes.models.SettingAction
import com.harsh.notes.ui.BaseActivity
import com.harsh.notes.ui.notesscreen.NotesActivity


class SettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingScreen(handleAction)
        }
    }

    private val handleAction = { action: SettingAction ->
        handleAction(action)
    }

    private fun handleAction(action: SettingAction) {
        when (action) {
            is SettingAction.ClickBack -> finish()
            is SettingAction.RestoreData -> restoreData()
            is SettingAction.OpenDraftNote -> openNotesActivity()
            else -> Unit
        }
    }

    private fun restoreData() {
        // todo handle
    }

    private fun openNotesActivity() {
        startActivity(NotesActivity.getIntent(this, isDraftScreen = true))
        finish()
    }
}