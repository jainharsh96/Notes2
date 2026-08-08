package com.notes.shared.ui.createnotescreen

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.notes.shared.ui.uientity.NoteEntity

interface CreateNoteContract {

    @Immutable
    data class State(val isLoading : Boolean = true, val originalNote: NoteEntity? = null, val enteredMsg: TextFieldValue = TextFieldValue(""), val showSystemKeyboard : Boolean = true) {

        fun hasNote() = originalNote?.body?.isNotEmpty() ?: false

        companion object {
            fun initialState() = State()
        }
    }

    sealed class Event{
        object ClickBack : Event()
        object ClickRecordNotes : Event()
        object ClickChangeKeyboard : Event()

        object HideKeyboard : Event()
        object ClickUndo : Event()
        object SaveNote : Event()
        object FetchNote : Event()
        data class OnType(val msg: TextFieldValue) : Event()
        data class AddMessage(val msg: TextFieldValue) : Event()
    }

    sealed class SideEffect{
        object ClickBack : SideEffect()
        object StartRecordNotes : SideEffect()
        object SavedNote : SideEffect()
        data class ShowError(val msg : String) : SideEffect()
    }
}