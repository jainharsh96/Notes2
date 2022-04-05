package com.harsh.notes.ui.notesscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.models.Note
import com.harsh.notes.models.NotesAction
import com.harsh.notes.models.NotesState
import com.harsh.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepository: NotesRepository) :
    ViewModel() {

    var notesState: NotesState by mutableStateOf(NotesState.NoData)

    var isDraftScreen = false

    private val _handleOnUi = MutableSharedFlow<NotesAction>()
    val handleOnUi = _handleOnUi.asSharedFlow()

    val handleAction = { action: NotesAction ->
        handleAction(action)
    }

    private fun handleAction(action: NotesAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when (action) {
                is NotesAction.FetchNotes -> fetchNotes(action.state)
                is NotesAction.DeleteNote -> deleteNote(action.noteId)
                is NotesAction.DraftNote -> draftNote(action.noteId)
                is NotesAction.InsertNote -> insertNote(action.note)
                is NotesAction.ClickBack -> _handleOnUi.emit(action)
                is NotesAction.OpenNote -> if (isDraftScreen.not()) _handleOnUi.emit(action)
                is NotesAction.AddNotes -> _handleOnUi.emit(action)
                is NotesAction.RecordNotes -> _handleOnUi.emit(action)
                is NotesAction.OpenSettings -> _handleOnUi.emit(action)
            }
        }
    }

    private suspend fun insertNote(note: Note) {
        notesRepository.insertNote(note)
    }

    private suspend fun deleteNote(noteId: Int) {
        notesRepository.deleteNote(noteId)
    }

    private suspend fun draftNote(noteId: Int) {
        notesRepository.draftNote(noteId)
    }

    private suspend fun fetchNotes(state: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.fetchAllNotes(state).catch { emptyList<Note>() }.collect { notes ->
                notesState = if (notes.isEmpty()) {
                    NotesState.NoData
                } else {
                    NotesState.Notes(notes)
                }
            }
        }
    }
}