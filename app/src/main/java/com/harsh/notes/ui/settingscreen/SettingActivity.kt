package com.harsh.notes.ui.settingscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.harsh.notes.ui.BaseActivity
import com.harsh.notes.ui.notesscreen.NotesActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    private val viewModel by viewModels<SettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          //  SettingScreen(::sideEffect)
        }
    }

    private fun sideEffect(action: SettingContract.Effect) {
        when (action) {
            is SettingContract.Effect.ClickBack -> finish()
            is SettingContract.Effect.RestoreData -> restoreData()
            is SettingContract.Effect.OpenDraftNote -> openNotesActivity()
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