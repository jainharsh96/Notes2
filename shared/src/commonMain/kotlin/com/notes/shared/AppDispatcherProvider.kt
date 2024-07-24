package com.notes.shared

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatcherProvider {
    val Default : CoroutineDispatcher
    val Main : CoroutineDispatcher
    val IO : CoroutineDispatcher
    val Unconfined : CoroutineDispatcher
}

