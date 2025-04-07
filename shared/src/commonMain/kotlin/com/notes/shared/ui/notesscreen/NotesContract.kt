package com.notes.shared.ui.notesscreen

import com.notes.shared.coreUi.UniDirectionalViewModel
import com.notes.shared.ui.uientity.NoteEntity

interface NotesContract :
    UniDirectionalViewModel<NotesContract.State, NotesContract.Event, NotesContract.SideEffect> {

    data class State(
        val isDraftState: Boolean,
        val alertDialogState : AlertDialogState? = null,
        val notes: List<NoteEntity>?,
        val confirmToDeleteNoteId: Int? = null
    ) {
        companion object {
            fun initialState(isDraftState: Boolean) =
                State(isDraftState = isDraftState, notes = null)
        }
    }

    data class AlertDialogState(val errorMsg : String? = null)

    sealed class Event {
        object ClickBack : Event()
        object AddNotes : Event()
        object RecordNotes : Event()
        object OpenSettings : Event()
        object DismissConfirmToDeleteNote : Event()
        object IsDraftScreen : Event()
        data class OpenNote(val noteId: Int) : Event()
        object FetchNotes : Event()
        data class InsertNote(val note: NoteEntity) : Event()
        data class ConfirmDeleteNote(val noteId: Int) : Event()
        data class DeleteNote(val noteId: Int) : Event()
        data class DraftNote(val noteId: Int) : Event()
        data class RestoreNote(val noteId: Int) : Event()
        data class EnteredPassword(val password: String?) : Event()
        // add load more notes event
    }

    sealed class SideEffect {
        object ClickBack : SideEffect()
        object AddNotes : SideEffect()
        object RecordNotes : SideEffect()
        object OpenSettings : SideEffect()
        data class OpenNote(val noteId: Int) : SideEffect()
    }
}