package com.harsh.notes.ui.createnotescreen

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.harsh.notes.coreUi.UniDirectionalViewModel
import com.harsh.notes.db.Note

interface CreateNoteContract : UniDirectionalViewModel<CreateNoteContract.State, CreateNoteContract.Event, CreateNoteContract.SideEffect> {

    @Immutable
    data class State(val originalNote : Note? = null, val enteredMsg : String = ""){    // todo create textinputfield for enter value

        fun hasNote() = originalNote?.body?.isNotEmpty() ?: false
        companion object{
            fun initialState() = State()
        }
    }

    sealed class Event{
        object ClickBack : Event()
        object ClickRecordNotes : Event()
        object ClickUndo : Event()
        object SaveNote : Event()
        data class FetchNote(val noteId: Int?) : Event()
        data class OnType(val msg: String) : Event()
        data class AddMessage(val msg: String) : Event()
    }

    sealed class SideEffect{
        object ClickBack : SideEffect()
        object ClickRecordNotes : SideEffect()
        object SavedNote : SideEffect()
        object NoteRendered : SideEffect()
        data class ShowError(val msg : String) : SideEffect()
    }
}