package com.harsh.notes.ui.createnotescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.models.CreateNoteAction
import com.harsh.notes.models.Note
import com.harsh.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val notesRepository: NotesRepository) :
    ViewModel() {

    private val _handleOnUi = MutableSharedFlow<CreateNoteAction>()
    val handleOnUi = _handleOnUi.asSharedFlow()

    private var originalNote: Note? = null

    var hasNote by mutableStateOf(false)
        private set

    var noteState by mutableStateOf("")

    val handleAction = { action: CreateNoteAction ->
        handleAction(action)
    }

    private fun handleAction(action: CreateNoteAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is CreateNoteAction.ClickBack -> _handleOnUi.emit(action)
                is CreateNoteAction.ClickRecordNotes -> _handleOnUi.emit(action)
                is CreateNoteAction.ClickUndo -> clickUndo()
                is CreateNoteAction.SaveNote -> insertNote()
                is CreateNoteAction.FetchNote -> fetchNote(action.noteId)
                else -> Unit
            }
        }
    }

    private fun clickUndo() {
        noteState = originalNote?.body ?: ""
    }

    private suspend fun insertNote() {
        if (noteState.isNotEmpty()) {
            val note = originalNote?.copy(body = noteState, updatedDate = Date()) ?: Note(
                body = noteState, createdDate = Date(),
                updatedDate = Date()
            )
            val isSaved = notesRepository.updateOrInsertNote(note)
            if (isSaved > 0) {
                _handleOnUi.emit(CreateNoteAction.SavedNote)
            }
        }
    }

    private suspend fun fetchNote(noteId: Int?) {
        noteId?.let {
            originalNote = notesRepository.fetchNote(noteId = noteId)
            hasNote = originalNote?.body?.isNotEmpty() ?: false
            noteState = originalNote?.body ?: ""
        }
        delay(50)
        _handleOnUi.emit(CreateNoteAction.NoteRendered)
    }
}