package com.notes.shared

import android.content.Context

object NotesAndroidDependenciesInitializer {

    fun init(context: Context, notesSyncManager: NotesSyncManager) {
        NotesDependencies.init(
            notesSyncManager = notesSyncManager,
            dataStore = DataStoreAndroidImpl(dataStore = DataStoreProvider.get(context)),
            databasePasswordProvider = DatabasePasswordProviderAndroidImpl(dataStore = DataStoreProvider.get(context))
        )
    }

    fun clearData() {
        NotesDependencies.clearData()
    }
}