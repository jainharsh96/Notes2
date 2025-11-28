package com.notes.shared

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.notes.shared.di.NotesKoin
import com.notes.shared.ui.NotesApp
import org.koin.core.context.startKoin

fun main() = application {
    NotesDependencies.init(
        databasePasswordProvider = DatabasePasswordProviderJvmImpl(),
        dataStore = DataStoreJvmImpl()
    )
    NotesKoin.init()
    Window(
        title = "Notes Desktop",
        onCloseRequest = ::exitApplication,
    ) {
        NotesApp() // shared composable UI
    }
}