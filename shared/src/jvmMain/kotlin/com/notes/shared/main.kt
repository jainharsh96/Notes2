package com.notes.shared

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.notes.shared.ui.NotesApp
import com.notes.shared.utils.NotesLogger

fun main() = application {
    runCatching {
        NotesDependencies.init(
            isDebugBuild = true,  // todo
            databasePasswordProvider = DatabasePasswordProviderJvmImpl(),
            dataStore = DataStoreJvmImpl(),
            notesDatabaseHelper = NotesDatabaseHelperJVMImpl(),
        )
        Window(
            title = "Notes Desktop",
            onCloseRequest = ::exitApplication,
        ) {
            NotesApp() // shared composable UI
        }
    }.onFailure {
        NotesLogger.inMemoryLog("DesktopMain", "exception ${it.message}")
        throw it
    }
}