package com.harsh.notes.ui.notesscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.harsh.notes.models.Note
import com.harsh.notes.ui.BaseActivity
import com.harsh.notes.ui.createnotescreen.CreateNoteActivity
import com.harsh.notes.ui.settingscreen.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotesActivity : BaseActivity() {

    private val viewModel by viewModels<NotesViewModel>()

    companion object {
        private val TAG = NotesActivity::class.java.simpleName
        private const val INTENT_DRAFT_SCREEN = "intent_draft"

        fun getIntent(context: Context, isDraftScreen: Boolean = false): Intent {
            return Intent(context, NotesActivity::class.java).apply {
                putExtra(INTENT_DRAFT_SCREEN, isDraftScreen)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isDraftScreen = isDraftScreen()
        setContent {
            NotesScreen(viewModel)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.event(NotesContract.Event.FetchNotes(if (viewModel.isDraftScreen) Note.DRAFTED else Note.SAVED))
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is NotesContract.SideEffect.ClickBack -> finish()
                    is NotesContract.SideEffect.OpenNote -> openNote(effect.noteId)
                    is NotesContract.SideEffect.AddNotes -> clickAddNote()
                    is NotesContract.SideEffect.RecordNotes -> clickRecordNote()
                    is NotesContract.SideEffect.OpenSettings -> clickSetting()
                    else -> Unit
                }
            }
        }
    }

    private fun clickSetting() {
        startActivity(SettingActivity.getIntent(this))
    }

    private fun clickRecordNote() {
        startActivity(CreateNoteActivity.getIntent(this, openRecording = true))
    }

    private fun clickAddNote() {
        startActivity(CreateNoteActivity.getIntent(this))
    }

    private fun openNote(noteId: Int) {
        startActivity(CreateNoteActivity.getIntent(this, noteId = noteId))
    }

    private fun isDraftScreen() = intent.getBooleanExtra(INTENT_DRAFT_SCREEN, false)
}