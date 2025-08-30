package com.notes.shared.utils

import kotlinx.coroutines.channels.SendChannel

// todo optimize this
suspend fun <T> SendChannel<T>.sendAndClose(value: T) {
    try {
        send(value) // suspends if needed
    } catch (e: Exception) {

    } finally {
        close()
    }
}