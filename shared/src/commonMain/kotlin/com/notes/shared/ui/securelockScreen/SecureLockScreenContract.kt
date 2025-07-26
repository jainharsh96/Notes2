package com.notes.shared.ui.securelockScreen

interface SecureLockScreenContract {

    data class State(val passwordState : PasswordState, val enteredPassword: String, val representedPassword : String) {
        companion object {
            fun initialState() = State(passwordState = PasswordState.ENTER_PASS, enteredPassword = "", representedPassword = "")
        }

        fun getPasswordHeader() = when(passwordState){
            PasswordState.ENTER_PASS -> "Enter Password"
            PasswordState.SET_PASS -> "Set Password"
            PasswordState.RE_ENTER_PASS -> "Re Enter Password"
        }
    }

    enum class PasswordState { ENTER_PASS, SET_PASS, RE_ENTER_PASS }

    sealed class Event {
        object ClickBack : Event()
        data class OnEnterPassword(val password: String) : Event()
        data class OnClickAction(val password: String) : Event()
    }

    sealed class SideEffect {
        object GoBack : SideEffect()
        object GoForward : SideEffect()
    }
}