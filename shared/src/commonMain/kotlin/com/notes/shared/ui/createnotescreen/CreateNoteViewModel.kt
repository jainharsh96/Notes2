package com.notes.shared.ui.createnotescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.repository.NotesRepository
import com.notes.shared.ui.BaseViewModel
import com.notes.shared.ui.NotesRoutes
import com.notes.shared.ui.notesscreen.NotesContract
import com.notes.shared.ui.uientity.NoteEntity
import com.notes.shared.utils.DateFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateNoteViewModel constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dispatcher: AppDispatcherProvider
) : BaseViewModel<CreateNoteContract.State, CreateNoteContract.Event, CreateNoteContract.SideEffect>() {

    private val noteId : Int? = savedStateHandle[NotesRoutes.ARG_NOTES_ID]
    private val isOpenRecording : Boolean = savedStateHandle[NotesRoutes.ARG_OPEN_RECORDING] ?: false

    private val _state = MutableStateFlow(CreateNoteContract.State.initialState())
    override val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CreateNoteContract.SideEffect>()
    override val sideEffect = _sideEffect.asSharedFlow()

    override fun event(event: CreateNoteContract.Event) {
        launchCoroutine {
            when (event) {
                CreateNoteContract.Event.ClickBack -> _sideEffect.emit(CreateNoteContract.SideEffect.ClickBack)
                CreateNoteContract.Event.ClickRecordNotes -> _sideEffect.emit(CreateNoteContract.SideEffect.StartRecordNotes)
                CreateNoteContract.Event.ClickUndo -> clickUndo()
                is CreateNoteContract.Event.FetchNote -> fetchNote(noteId = noteId)
                CreateNoteContract.Event.SaveNote -> insertNote()
                is CreateNoteContract.Event.OnType -> onType(event.msg)
                is CreateNoteContract.Event.AddMessage -> addMsg(event.msg)
                CreateNoteContract.Event.ClickChangeKeyboard -> switchKeyboard()
                CreateNoteContract.Event.HideKeyboard -> switchKeyboard()
            }
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

    private fun switchKeyboard() {
        _state.update {
            it.copy(
                showSystemKeyboard = it.showSystemKeyboard.not()
            )
        }
    }

    private suspend fun insertNote() = withContext(dispatcher.IO) {
        with(_state.value) {
            if (enteredMsg.isNotEmpty()) {
                val currentDateTime = DateFormatter.currentDateTime(DateFormatter.NOTE_DATE_FORMAT)
                val note = originalNote?.copy(body = enteredMsg, updatedDate = currentDateTime) ?: NoteEntity(
                    body = enteredMsg, createdDate = currentDateTime,
                    updatedDate = currentDateTime
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
        val originalNote = noteId?.let { notesRepository.fetchNote(noteId = noteId) }
        _state.update {
            it.copy(
                isLoading = false,
                originalNote = originalNote,
                enteredMsg = originalNote?.body ?: ""
            )
        }
        delay(50)
        if (isOpenRecording){
            _sideEffect.emit(CreateNoteContract.SideEffect.StartRecordNotes)
        }
    }
}