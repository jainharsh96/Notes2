package com.notes.shared.ui.securelockScreen

import androidx.compose.ui.text.input.TextFieldValue
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

class SecureLockScreenViewmodel(
    private val screenLockUtil: ScreenLockUtil,
) :
    BaseViewModel<SecureLockScreenContract.State, SecureLockScreenContract.Event, SecureLockScreenContract.SideEffect>() {

    private val _state = MutableStateFlow(SecureLockScreenContract.State.initialState())
    override val state: StateFlow<SecureLockScreenContract.State>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SecureLockScreenContract.SideEffect>()
    override val sideEffect: SharedFlow<SecureLockScreenContract.SideEffect>
        get() = _sideEffect.asSharedFlow()

    private var matchingPassword : String? = null

    init {
        launchCoroutine {
            withContext(Dispatchers.IO) {
                matchingPassword = screenLockUtil.getUnlockPassword()
                if (matchingPassword.isNullOrEmpty()){
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
                                    it.copy(passwordState = SecureLockScreenContract.PasswordState.RE_ENTER_PASS, enteredPassword = TextFieldValue(
                                        ""
                                    ))
                                }
                            }
                        } else if (_state.value.passwordState == SecureLockScreenContract.PasswordState.RE_ENTER_PASS){
                            if (event.password.text == matchingPassword){
                                screenLockUtil.setUnlockPassword(event.password.text)
                                screenLockUtil.setScreenUnlocked()
                                _sideEffect.emit(SecureLockScreenContract.SideEffect.GoForward)
                            }
                        } else {
                            if (event.password.text == matchingPassword){
                                screenLockUtil.setScreenUnlocked()
                                _sideEffect.emit(SecureLockScreenContract.SideEffect.GoForward)
                            }
                        }
                    }

                    is SecureLockScreenContract.Event.OnEnterPassword -> {
                        if (event.password.text.length <= 8){
                            _state.update {
                                it.copy(enteredPassword = event.password)
                            }
                        }
                    }
                }
            }
        }
    }
}