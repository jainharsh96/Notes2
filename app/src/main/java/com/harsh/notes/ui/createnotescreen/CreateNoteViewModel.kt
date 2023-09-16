package com.harsh.notes.ui.createnotescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.AppDispatcherProvider
import com.harsh.notes.db.Note
import com.harsh.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val dispatcher: AppDispatcherProvider
) : ViewModel(), CreateNoteContract {

    private val _state = MutableStateFlow(CreateNoteContract.State.initialState())
    override val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CreateNoteContract.SideEffect>()
    override val sideEffect = _sideEffect.asSharedFlow()

    override fun event(event: CreateNoteContract.Event) {
        viewModelScope.launch {
            when (event) {
                CreateNoteContract.Event.ClickBack -> _sideEffect.emit(CreateNoteContract.SideEffect.ClickBack)
                CreateNoteContract.Event.ClickRecordNotes -> _sideEffect.emit(CreateNoteContract.SideEffect.ClickRecordNotes)
                CreateNoteContract.Event.ClickUndo -> clickUndo()
                is CreateNoteContract.Event.FetchNote -> fetchNote(event.noteId)
                CreateNoteContract.Event.SaveNote -> insertNote()
                is CreateNoteContract.Event.OnType -> onType(event.msg)
                is CreateNoteContract.Event.AddMessage -> addMsg(event.msg)
            }
        }
    }

    private fun scopeIO(content: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            content.invoke()
        }
    }

    private fun onType(newMsg: String) {
        _state.update {
            it.copy(enteredMsg = newMsg)
        }
    }

    private fun addMsg(newMsg: String) {
        _state.update {
            it.copy(enteredMsg = it.enteredMsg.plus(newMsg))
        }
    }

    private fun clickUndo() {
        _state.update {
            it.copy(
                enteredMsg = it.originalNote?.body ?: ""
            )
        }
    }

    private suspend fun insertNote() = withContext(dispatcher.IO) {
        with(_state.value) {
            if (enteredMsg.isNotEmpty()) {
                val note = originalNote?.copy(body = enteredMsg, updatedDate = Date()) ?: Note(
                    body = enteredMsg, createdDate = Date(),
                    updatedDate = Date()
                )
                val isSaved = notesRepository.updateOrInsertNote(note)
                if (isSaved > 0) {
                    _sideEffect.emit(CreateNoteContract.SideEffect.SavedNote)
                } else {
                    _sideEffect.emit(CreateNoteContract.SideEffect.ShowError("note not saved"))
                }
            }
        }
    }

    private suspend fun fetchNote(noteId: Int?) = withContext(dispatcher.IO) {
        noteId?.let {
            _state.update {
                val originalNote = notesRepository.fetchNote(noteId = noteId)
                it.copy(
                    originalNote = originalNote,
                    enteredMsg = originalNote?.body ?: ""
                )
            }
        }
        delay(50)
        _sideEffect.emit(CreateNoteContract.SideEffect.NoteRendered)
    }
}