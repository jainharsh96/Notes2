package com.harsh.notes.coreUi

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UniDirectionalViewModel<STATE, EVENT, EFFECT> {
    val state: StateFlow<STATE>
    val sideEffect: SharedFlow<EFFECT>
    fun event(event: EVENT)
}