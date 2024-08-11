package com.notes.shared

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

interface AppDispatcherProvider {
    val Default : CoroutineDispatcher
    val Main : CoroutineDispatcher
    val IO : CoroutineDispatcher
    val Unconfined : CoroutineDispatcher
}

class AppDispatcherImpl constructor() : AppDispatcherProvider {
    override val Default: CoroutineDispatcher = Dispatchers.Default
    override val Main: CoroutineDispatcher = Dispatchers.Main
    override val IO: CoroutineDispatcher = Dispatchers.IO
    override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}