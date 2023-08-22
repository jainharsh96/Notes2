package com.harsh.notes.ui.notesscreen

import com.harsh.notes.coreUi.UniDirectionalViewModel
import com.harsh.notes.models.Note

interface NotesContract :
    UniDirectionalViewModel<NotesContract.State, NotesContract.Event, NotesContract.SideEffect> {

    sealed class State {
        object NoData : State()
        data class Notes(val notes: List<Note>, val confirmToDeleteNoteId: Int? = null) : State()
    }

    sealed class Event {
        object ClickBack : Event()
        object AddNotes : Event()
        object RecordNotes : Event()
        object OpenSettings : Event()
        object DismissConfirmToDeleteNote : Event()
        object IsDraftScreen : Event()
        data class OpenNote(val noteId: Int) : Event()
        data class FetchNotes(val state: Int) : Event()
        data class InsertNote(val note: Note) : Event()
        data class ConfirmDeleteNote(val noteId: Int) : Event()
        data class DeleteNote(val noteId: Int) : Event()
        data class DraftNote(val noteId: Int) : Event()
        data class RestoreNote(val noteId: Int) : Event()
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