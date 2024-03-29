package com.harsh.notes.ui.createnotescreen

import androidx.compose.runtime.Immutable
import com.harsh.notes.coreUi.UniDirectionalViewModel
import com.harsh.notes.db.Note

interface CreateNoteContract : UniDirectionalViewModel<CreateNoteContract.State, CreateNoteContract.Event, CreateNoteContract.SideEffect> {

    @Immutable
    data class State(val originalNote : Note? = null, val enteredMsg : String = ""){

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
        object FetchNote : Event()
        data class OnType(val msg: String) : Event()
        data class AddMessage(val msg: String) : Event()
    }

    sealed class SideEffect{
        object ClickBack : SideEffect()
        object StartRecordNotes : SideEffect()
        object SavedNote : SideEffect()
        data class ShowError(val msg : String) : SideEffect()
    }
}