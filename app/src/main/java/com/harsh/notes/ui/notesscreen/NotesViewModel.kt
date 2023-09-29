package com.harsh.notes.ui.notesscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.AppDispatcherProvider
import com.harsh.notes.db.Note
import com.harsh.notes.repository.NotesRepository
import com.harsh.notes.ui.NotesRoutes.ARG_IS_DRAFT_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val dispatcher: AppDispatcherProvider
) : ViewModel(), NotesContract {

    val isDraftScreen = savedStateHandle[ARG_IS_DRAFT_SCREEN] ?: false   // move this in state also

    private val _state = MutableStateFlow<NotesContract.State>(NotesContract.State.NoData)
    override val state: StateFlow<NotesContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<NotesContract.SideEffect>()
    override val sideEffect: SharedFlow<NotesContract.SideEffect> = _sideEffect

    override fun event(event: NotesContract.Event) {
        viewModelScope.launch {
            when (event) {
                is NotesContract.Event.FetchNotes -> fetchNotes(event.state)
                is NotesContract.Event.ConfirmDeleteNote -> confirmDeleteNote(event.noteId)
                is NotesContract.Event.DismissConfirmToDeleteNote -> confirmDeleteNote(null)
                is NotesContract.Event.DeleteNote -> deleteNote(event.noteId)
                is NotesContract.Event.DraftNote -> draftNote(event.noteId)
                is NotesContract.Event.RestoreNote -> restoreNote(event.noteId)
                is NotesContract.Event.InsertNote -> insertNote(event.note)
                is NotesContract.Event.ClickBack -> _sideEffect.emit(NotesContract.SideEffect.ClickBack)
                is NotesContract.Event.OpenNote -> if (isDraftScreen.not()) _sideEffect.emit(
                    NotesContract.SideEffect.OpenNote(event.noteId)
                )

                is NotesContract.Event.AddNotes -> _sideEffect.emit(NotesContract.SideEffect.AddNotes)
                is NotesContract.Event.RecordNotes -> _sideEffect.emit(NotesContract.SideEffect.RecordNotes)
                is NotesContract.Event.OpenSettings -> _sideEffect.emit(NotesContract.SideEffect.OpenSettings)
                is NotesContract.Event.IsDraftScreen -> Unit
            }
        }
    }

    private fun confirmDeleteNote(noteId: Int?) {
        _state.update {
            if (it is NotesContract.State.Notes) {
                it.copy(confirmToDeleteNoteId = noteId)
            } else it
        }
    }

    private suspend fun insertNote(note: Note) = withContext(dispatcher.IO) {
        notesRepository.insertNote(note)
    }

    private suspend fun deleteNote(noteId: Int) = withContext(dispatcher.IO) {
        notesRepository.deleteNote(noteId)
        confirmDeleteNote(null)
    }

    private suspend fun draftNote(noteId: Int) = withContext(dispatcher.IO) {
        notesRepository.changeNoteState(noteId = noteId, state = Note.DRAFTED)
    }

    private suspend fun restoreNote(noteId: Int) = withContext(dispatcher.IO) {
        notesRepository.changeNoteState(noteId = noteId, state = Note.SAVED)
    }

    private suspend fun fetchNotes(state: Int) = withContext(dispatcher.IO) {
        notesRepository.fetchAllNotes(state).catch { emptyList<Note>() }.collect { notes ->
            _state.update {
                if (notes.isEmpty()) {
                    NotesContract.State.NoData
                } else {
                    NotesContract.State.Notes(notes)
                }
            }
        }
    }

    fun restoreDeletedNote(noteId: Int) = viewModelScope.launch(dispatcher.IO) {
        notesRepository.restoreDeletedNote(noteId)
    }
}