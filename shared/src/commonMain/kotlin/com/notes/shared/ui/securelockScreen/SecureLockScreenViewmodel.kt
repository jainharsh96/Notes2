package com.notes.shared.ui.securelockScreen

import com.notes.shared.ScreenLockUtil
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

class SecureLockScreenViewmodel() :
    BaseViewModel<SecureLockScreenContract.State, SecureLockScreenContract.Event, SecureLockScreenContract.SideEffect>() {

    private val _state = MutableStateFlow(SecureLockScreenContract.State.initialState())
    override val state: StateFlow<SecureLockScreenContract.State>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SecureLockScreenContract.SideEffect>()
    override val sideEffect: SharedFlow<SecureLockScreenContract.SideEffect>
        get() = _sideEffect.asSharedFlow()

    private var matchingPassword : String? = null

    init {
        matchingPassword = ScreenLockUtil.getUnlockPassword()
        if (matchingPassword.isNullOrEmpty()){
            _state.update {
                it.copy(passwordState = SecureLockScreenContract.PasswordState.SET_PASS)
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
                        if (event.password.isEmpty()) return@withContext

                        if (_state.value.passwordState == SecureLockScreenContract.PasswordState.SET_PASS) {
                            if (event.password.length >= 4 && event.password.length <= 8) {
                                matchingPassword = event.password
                                _state.update {
                                    it.copy(passwordState = SecureLockScreenContract.PasswordState.RE_ENTER_PASS, enteredPassword = "", representedPassword = "")
                                }
                            }
                        } else if (_state.value.passwordState == SecureLockScreenContract.PasswordState.RE_ENTER_PASS){
                            if (event.password == matchingPassword){
                                ScreenLockUtil.setUnlockPassword(event.password)
                                ScreenLockUtil.setScreenUnlocked()
                                _sideEffect.emit(SecureLockScreenContract.SideEffect.GoForward)
                            }
                        } else {
                            if (event.password == matchingPassword){
                                ScreenLockUtil.setScreenUnlocked()
                                _sideEffect.emit(SecureLockScreenContract.SideEffect.GoForward)
                            }
                        }
                    }

                    is SecureLockScreenContract.Event.OnEnterPassword -> {
                        if (event.password.length <= 8){
                            _state.update {
                                it.copy(enteredPassword = event.password, representedPassword = "*".repeat(event.password.length))
                            }
                        }
                    }
                }
            }
        }
    }
}