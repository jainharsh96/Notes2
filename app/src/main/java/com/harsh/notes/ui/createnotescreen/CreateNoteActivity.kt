package com.harsh.notes.ui.createnotescreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.harsh.notes.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CreateNoteActivity : BaseActivity() {

    companion object {
        private const val INTENT_NOTE_ID = "note_id"
        private const val INTENT_OPEN_RECORDING = "open_recording"
        fun getIntent(context: Context, noteId: Int? = null, openRecording: Boolean = false) =
            Intent(context, CreateNoteActivity::class.java).apply {
                putExtra(
                    INTENT_NOTE_ID,
                    noteId
                )
                putExtra(INTENT_OPEN_RECORDING, openRecording)
            }
    }

    private val viewModel by viewModels<CreateNoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           // CreateNoteScreen(viewModel)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.sideEffect.collect { sideEffect ->
                when (sideEffect) {
                    CreateNoteContract.SideEffect.ClickBack, CreateNoteContract.SideEffect.SavedNote -> finish()
                    CreateNoteContract.SideEffect.ClickRecordNotes -> clickRecordNote()
                    CreateNoteContract.SideEffect.NoteRendered -> afterRenderNote()
                    is CreateNoteContract.SideEffect.ShowError -> Unit // todo show toast
                }
            }
        }
        viewModel.event(CreateNoteContract.Event.FetchNote(getNoteByIntent()))
    }

    private fun afterRenderNote() {
        if (isOpenRecording()) {
            startRecognizeVoice()
        }
    }

    private fun clickRecordNote() {
        startRecognizeVoice()
    }

    override fun onRecognizeVoiceText(texts: ArrayList<String?>?) {
        viewModel.event(CreateNoteContract.Event.AddMessage(texts?.joinToString("\n") ?: ""))
    }

    private fun getNoteByIntent() = intent.getIntExtra(INTENT_NOTE_ID, -1)

    private fun isOpenRecording() = intent.getBooleanExtra(INTENT_OPEN_RECORDING, false)
}