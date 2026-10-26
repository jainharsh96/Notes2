package com.notes.shared

import com.notes.shared.coreUi.ClipboardManagerIOSImpl
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

/*
call this function in the iOS AppDelegate to initialize dependencies.
 */
object NotesIOSDependenciesInitializer {
    @OptIn(ExperimentalNativeApi::class)
    fun init() {
        NotesDependencies.init(
            isDebugBuild = Platform.isDebugBinary,
            dataStore = DataStoreIOSImpl(dataStore = DataStoreProvider.dataStore),
            databasePasswordProvider = DatabasePasswordProviderIOSImpl(),
            notesDatabaseHelper = NotesDatabaseHelperIOSImpl(),
            clipboardManager = ClipboardManagerIOSImpl()
        )
    }
}