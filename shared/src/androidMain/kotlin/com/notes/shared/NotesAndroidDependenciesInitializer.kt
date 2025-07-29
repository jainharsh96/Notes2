package com.notes.shared

import android.content.Context
import com.notes.shared.coreUi.ClipboardManagerAndroidImpl

object NotesAndroidDependenciesInitializer {

    fun init(context: Context, notesSyncManager: NotesSyncManager) {
        NotesDependencies.init(
            notesSyncManager = notesSyncManager,
            dataStore = DataStoreAndroidImpl(dataStore = DataStoreProvider.get(context)),
            databasePasswordProvider = DatabasePasswordProviderAndroidImpl(dataStore = DataStoreProvider.get(context)),
            clipboardManager = ClipboardManagerAndroidImpl(context)
        )
    }

    fun clearData() {
        NotesDependencies.clearData()
    }
}