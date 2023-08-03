package com.harsh.notes.ui.notesscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.AppDispatcherProvider
import com.harsh.notes.models.Note
import com.harsh.notes.models.NotesAction
import com.harsh.notes.models.NotesState
import com.harsh.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepository: NotesRepository, private val dispatcher : AppDispatcherProvider) :
    ViewModel() {

    var notesState: NotesState by mutableStateOf(NotesState.NoData)

    var isDraftScreen = false

    private val _handleOnUi = MutableSharedFlow<NotesAction>()
    val handleOnUi = _handleOnUi.asSharedFlow()

    val handleAction = { action: NotesAction ->
        handleAction(action)
    }

    private fun handleAction(action: NotesAction) {
        viewModelScope.launch(dispatcher.IO) {
            when (action) {
                is NotesAction.FetchNotes -> fetchNotes(action.state)
                is NotesAction.ConfirmDeleteNote -> confirmDeleteNote(action.noteId)
                is NotesAction.DismissConfirmToDeleteNote -> confirmDeleteNote(null)
                is NotesAction.DeleteNote -> deleteNote(action.noteId)
                is NotesAction.DraftNote -> draftNote(action.noteId)
                is NotesAction.RestoreNote -> restoreNote(action.noteId)
                is NotesAction.InsertNote -> insertNote(action.note)
                is NotesAction.ClickBack -> _handleOnUi.emit(action)
                is NotesAction.OpenNote -> if (isDraftScreen.not()) _handleOnUi.emit(action)
                is NotesAction.AddNotes -> _handleOnUi.emit(action)
                is NotesAction.RecordNotes -> _handleOnUi.emit(action)
                is NotesAction.OpenSettings -> _handleOnUi.emit(action)
            }
        }
    }

    private fun confirmDeleteNote(noteId: Int?) {
        if (notesState is NotesState.Notes){
            notesState = (notesState as NotesState.Notes).copy(confirmToDeleteNoteId = noteId)
        }
    }

    private suspend fun insertNote(note: Note) {
        notesRepository.insertNote(note)
    }

    private suspend fun deleteNote(noteId: Int) {
        notesRepository.deleteNote(noteId)
        confirmDeleteNote(null)
    }

    private suspend fun draftNote(noteId: Int) {
        notesRepository.changeNoteState(noteId = noteId, state = Note.DRAFTED)
    }

    private suspend fun restoreNote(noteId: Int) {
        notesRepository.changeNoteState(noteId = noteId, state = Note.SAVED)
    }

    private suspend fun fetchNotes(state: Int) {
        viewModelScope.launch(dispatcher.IO) {
            notesRepository.fetchAllNotes(state).catch { emptyList<Note>() }.collect { notes ->
                notesState = if (notes.isEmpty()) {
                    NotesState.NoData
                } else {
                    NotesState.Notes(notes)
                }
            }
        }
    }

    fun restoreDeletedNote(noteId: Int) = viewModelScope.launch(dispatcher.IO) {
        notesRepository.restoreDeletedNote(noteId)
    }

    suspend fun testingCoroutine() = withContext(dispatcher.IO){
        delay(5000)
        return@withContext notesRepository.deleteNote(123)
    }

    suspend fun testviewModelScope() = viewModelScope.launch(dispatcher.Main) {
        delay(5000)
        notesRepository.deleteNote(123)
    }
}