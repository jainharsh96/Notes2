package com.notes.shared.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.shared.utils.NotesLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<STATE, EVENT, EFFECT> : ViewModel() {
    abstract val state: StateFlow<STATE>
    abstract val sideEffect: SharedFlow<EFFECT>
    abstract fun event(event: EVENT)

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        // log exception here
        NotesLogger.inMemoryLog("BaseViewModel", "Throwable ${throwable.message}")
        NotesLogger.log("BaseViewModel", "Throwable ${throwable.message}")
    }

    fun launchCoroutine(content: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            content()
        }
    }
}