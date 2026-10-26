package com.notes.shared.ui.securelockScreen

import androidx.compose.ui.text.input.TextFieldValue
import com.notes.shared.NotesDependencies
import com.notes.shared.ScreenLockUtil
import com.notes.shared.domain.AppDatabaseState
import com.notes.shared.domain.NotesDbUseCase
import com.notes.shared.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class SecureLockScreenViewmodel(
    private val screenLockUtil: ScreenLockUtil,
    private val notesDbUseCase: NotesDbUseCase,
) :
    BaseViewModel<SecureLockScreenContract.State, SecureLockScreenContract.Event, SecureLockScreenContract.SideEffect>() {

    private val _state = MutableStateFlow(SecureLockScreenContract.State.initialState())
    override val state: StateFlow<SecureLockScreenContract.State>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SecureLockScreenContract.SideEffect>()
    override val sideEffect: SharedFlow<SecureLockScreenContract.SideEffect>
        get() = _sideEffect.asSharedFlow()

    private var matchingPassword: String? = null

    init {
        launchCoroutine {
            withContext(Dispatchers.IO) {
                matchingPassword = screenLockUtil.getUnlockPassword()
                if (matchingPassword.isNullOrEmpty()) {
                    _state.update {
                        it.copy(passwordState = SecureLockScreenContract.PasswordState.SET_PASS)
                    }
                }
            }
        }
    }

    override fun event(event: SecureLockScreenContract.Event) {
        launchCoroutine {
            withContext(Dispatchers.IO) {
                when (event) {
                    SecureLockScreenContract.Event.ClickBack -> {
                        _sideEffect.emit(SecureLockScreenContract.SideEffect.GoBack)
                    }

                    is SecureLockScreenContract.Event.OnClickAction -> {
                        if (event.password.text.isEmpty()) return@withContext

                        if (_state.value.passwordState == SecureLockScreenContract.PasswordState.SET_PASS) {
                            if (event.password.text.length >= 4 && event.password.text.length <= 8) {
                                matchingPassword = event.password.text
                                _state.update {
                                    it.copy(
                                        passwordState = SecureLockScreenContract.PasswordState.RE_ENTER_PASS,
                                        enteredPassword = TextFieldValue(
                                            ""
                                        )
                                    )
                                }
                            }
                        } else if (_state.value.passwordState == SecureLockScreenContract.PasswordState.RE_ENTER_PASS) {
                            if (event.password.text == matchingPassword) {
                                screenLockUtil.setUnlockPassword(event.password.text)
                                moveToNextStep()
                            }
                        } else {
                            if (event.password.text == matchingPassword) {
                                moveToNextStep()
                            }
                        }
                    }

                    is SecureLockScreenContract.Event.OnEnterLockPassword -> {
                        if (event.password.text.length <= 8) {
                            _state.update {
                                it.copy(enteredPassword = event.password)
                            }
                        }
                    }

                    is SecureLockScreenContract.Event.OnSelectedSetupNotesFlow -> {
                        val isRestoreFromCloud = event.flow == SetupNotesFlow.RESTORE_FROM_CLOUD
                        if (isRestoreFromCloud){
                            _state.update {
                                it.copy(setupNotesState = it.setupNotesState?.copy(isLoadingNotesFromCloud = true))
                            }

                            val result = getNotesSyncManager()?.restoreDataFromCloud()
                            val setupNotesFlow = if (result?.isSuccess() == true) {
                                SetupNotesFlow.RESTORE_FROM_CLOUD
                            } else {
                                null
                            }
                            _state.update {
                                it.copy(
                                    setupNotesState = it.setupNotesState?.copy(setupNotesFlow = setupNotesFlow, isLoadingNotesFromCloud = false)
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(setupNotesState = it.setupNotesState?.copy(setupNotesFlow = event.flow))
                            }
                        }
                    }

                    is SecureLockScreenContract.Event.OnConfirmNotesPassword -> {
                        val notesFlow = _state.value.setupNotesState?.setupNotesFlow
                        if (notesFlow != null) {
                            val isDbOpened = if (notesFlow == SetupNotesFlow.SETUP_FRESH_NOTES){
                                notesDbUseCase.setupNewDatabase(event.passCode)
                            } else {
                                notesDbUseCase.tryOpeningDbWithPassword(event.passCode)
                            }

                            if (isDbOpened) {
                                notesDbUseCase.saveDbPassword(event.passCode)
                                moveToNextStep()
                            } else {
                                _state.update {
                                    it.copy(
                                        setupNotesState = it.setupNotesState?.copy(errorMsg = "Invalid Password")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun moveToNextStep() {
        screenLockUtil.setScreenUnlocked()
        checkDbSetupStatus()
    }

    private suspend fun checkDbSetupStatus() {
        var dbState = notesDbUseCase.getAppDatabaseState()
        when(dbState){
            AppDatabaseState.INITIALIZED -> {
                gotoNextScreen()
                return
            }
            AppDatabaseState.UNINITIALIZED -> {
                val isInitialized = notesDbUseCase.tryInitDb()
                if (isInitialized) {
                    gotoNextScreen()
                    return
                } else {
                    notesDbUseCase.saveDbPassword("")  // intentionally set empty password to force user to set new password
                }
            }
            AppDatabaseState.DATABASE_FILE_NOT_PRESENT -> {
                notesDbUseCase.saveDbPassword("")  // intentionally set empty password to force user to set new password
            }
            AppDatabaseState.PASSWORD_NOT_PRESENT -> Unit
        }

        _state.update {
            it.copy(
                setupNotesState = SetupNotesState(
                    isCloudBackUpEnabled = getNotesSyncManager()?.hasSupportSync() == true,
                    isLocalBackupPresent = dbState == AppDatabaseState.UNINITIALIZED || dbState == AppDatabaseState.PASSWORD_NOT_PRESENT
                )
            )
        }
    }

    private fun getNotesSyncManager() = NotesDependencies.notesSyncManager

    private suspend fun gotoNextScreen() {
        _sideEffect.emit(SecureLockScreenContract.SideEffect.GoForward)
    }
}