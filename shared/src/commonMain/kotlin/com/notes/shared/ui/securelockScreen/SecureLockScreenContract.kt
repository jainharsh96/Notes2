package com.notes.shared.ui.securelockScreen

import androidx.compose.ui.text.input.TextFieldValue

interface SecureLockScreenContract {

    data class State(val passwordState : PasswordState, val enteredPassword: TextFieldValue) {
        companion object {
            fun initialState() = State(passwordState = PasswordState.ENTER_PASS, enteredPassword = TextFieldValue(""))
        }

        fun getPasswordHeader() = when(passwordState){
            PasswordState.ENTER_PASS -> "Enter Unlock app Password"
            PasswordState.SET_PASS -> "Set Unlock app Password First(min 4 and max 8 digits)"
            PasswordState.RE_ENTER_PASS -> "Re Enter Unlock app Password"
        }
    }

    enum class PasswordState { ENTER_PASS, SET_PASS, RE_ENTER_PASS }

    sealed class Event {
        object ClickBack : Event()
        data class OnEnterPassword(val password: TextFieldValue) : Event()
        data class OnClickAction(val password: TextFieldValue) : Event()
    }

    sealed class SideEffect {
        object GoBack : SideEffect()
        object GoForward : SideEffect()
    }
}