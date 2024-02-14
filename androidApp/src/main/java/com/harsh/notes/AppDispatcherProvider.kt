package com.harsh.notes

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface AppDispatcherProvider {
    val Default : CoroutineDispatcher
    val Main : CoroutineDispatcher
    val IO : CoroutineDispatcher
    val Unconfined : CoroutineDispatcher
}


class AppDispatcherImpl @Inject constructor() : AppDispatcherProvider {
    override val Default: CoroutineDispatcher = Dispatchers.Default
    override val Main: CoroutineDispatcher = Dispatchers.Main
    override val IO: CoroutineDispatcher = Dispatchers.IO
    override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}