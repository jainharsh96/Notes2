package com.notes.shared.ui.securelockScreen

import androidx.compose.ui.text.input.TextFieldValue

interface SecureLockScreenContract {

    data class State(
        val passwordState: PasswordState,
        val enteredPassword: TextFieldValue,
        val setupNotesState: SetupNotesState?,
    ) {
        companion object {
            fun initialState() = State(
                passwordState = PasswordState.ENTER_PASS,
                enteredPassword = TextFieldValue(""),
                setupNotesState = null
            )
        }

        fun getPasswordHeader() = when (passwordState) {
            PasswordState.ENTER_PASS -> "Enter Unlock app Password"
            PasswordState.SET_PASS -> "Set Unlock app Password First(min 4 and max 8 digits)"
            PasswordState.RE_ENTER_PASS -> "Re Enter Unlock app Password"
        }
    }

    enum class PasswordState { ENTER_PASS, SET_PASS, RE_ENTER_PASS }

    sealed class Event {
        object ClickBack : Event()
        data class OnEnterLockPassword(val password: TextFieldValue) : Event()
        data class OnClickAction(val password: TextFieldValue) : Event()

        data class OnSelectedSetupNotesFlow(val flow: SetupNotesFlow) : Event()

        data class OnConfirmNotesPassword(val passCode: String) : Event()
    }

    sealed class SideEffect {
        object GoBack : SideEffect()
        object GoForward : SideEffect()
    }
}

data class SetupNotesState(
    val isCloudBackUpEnabled : Boolean = false,
    val isLocalBackupPresent : Boolean = false,
    val setupNotesFlow: SetupNotesFlow? = null,
    val isLoadingNotesFromCloud : Boolean = false,
    val errorMsg : String? = null,
)

enum class SetupNotesFlow {
    RESTORE_FROM_CLOUD,
    RESTORE_FROM_LOCAL,
    SETUP_FRESH_NOTES
}

fun SetupNotesFlow.getFlowDescription() : String {
    return when (this) {
        SetupNotesFlow.RESTORE_FROM_CLOUD -> "Restore Notes from Google Drive"
        SetupNotesFlow.RESTORE_FROM_LOCAL -> "Restore Notes from Phone Storage"
        SetupNotesFlow.SETUP_FRESH_NOTES -> "Setup Fresh Notes"
    }
}