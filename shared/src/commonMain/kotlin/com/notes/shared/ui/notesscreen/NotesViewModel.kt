package com.notes.shared.ui.notesscreen

import androidx.lifecycle.SavedStateHandle
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.NotesDependencies
import com.notes.shared.ScreenLockUtil
import com.notes.shared.db.Note
import com.notes.shared.domain.NotesDbUseCase
import com.notes.shared.repository.NotesRepository
import com.notes.shared.ui.BaseViewModel
import com.notes.shared.ui.NotesRoutes.ARG_IS_DRAFT_SCREEN
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


class NotesViewModel constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val notesDbUseCase: NotesDbUseCase,
    private val dispatcher: AppDispatcherProvider
) : BaseViewModel<NotesContract.State, NotesContract.Event, NotesContract.SideEffect>() {

    private val isDraftScreen = savedStateHandle[ARG_IS_DRAFT_SCREEN] ?: false

    private val _state = MutableStateFlow(NotesContract.State.initialState(isDraftScreen))
    override val state: StateFlow<NotesContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<NotesContract.SideEffect>()
    override val sideEffect: SharedFlow<NotesContract.SideEffect> = _sideEffect.asSharedFlow()

    init {
        launchCoroutine {
            withContext(dispatcher.IO){
                if (ScreenLockUtil.isScreenUnLocked){
                    if (isDbPasswordSet()){
                        fetchNotes(if (isDraftScreen) Note.DRAFTED else Note.SAVED)
                    } else {
                        _state.update {
                            it.copy(alertDialogState = NotesContract.AlertDialogState())
                        }
                    }
                } else {
                    _state.update {
                        it.copy(unLockAppFirst = true)
                    }
                }
            }
        }
    }

    override fun event(event: NotesContract.Event) {
        launchCoroutine {
            when (event) {
                is NotesContract.Event.FetchNotes -> fetchNotes(if (isDraftScreen) Note.DRAFTED else Note.SAVED)
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
                is NotesContract.Event.EnteredPassword -> {
                    event.password?.let {
                        onEnterPassword(it)
                    }
                }
            }
        }
    }

    private suspend fun onEnterPassword(password : String) = withContext(dispatcher.IO){
        if (password.isNotEmpty()){
            NotesDependencies.databasePasswordProvider?.setPassword(password)
            if (notesDbUseCase.tryInitDb()){
                _state.update {
                    it.copy(alertDialogState = null)
                }
                fetchNotes(if (isDraftScreen) Note.DRAFTED else Note.SAVED)
            } else {
                _state.update {
                    it.copy(alertDialogState = NotesContract.AlertDialogState(errorMsg = "wrong password"))
                }
            }
        }
    }

    private suspend fun isDbPasswordSet() = withContext(dispatcher.IO){
        return@withContext notesDbUseCase.tryInitDb()
    }

    private fun confirmDeleteNote(noteId: Int?) {
        _state.update {
            it.copy(confirmToDeleteNoteId = noteId)
        }
    }

    private suspend fun insertNote(note: NoteEntity) = withContext(dispatcher.IO) {
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

    private fun fetchNotes(state: Int) = launchCoroutine {
        withContext(dispatcher.IO) {
            if (notesDbUseCase.isDBInitialized()){
                notesRepository.fetchAllNotes(state).catch { emptyList<Note>() }.collect { notes ->
                    _state.update {
                        it.copy(notes = notes)
                    }
                }
            }
        }
    }

    fun restoreDeletedNote(noteId: Int) = launchCoroutine {
        withContext(dispatcher.IO){
            notesRepository.restoreDeletedNote(noteId)
        }
    }
}