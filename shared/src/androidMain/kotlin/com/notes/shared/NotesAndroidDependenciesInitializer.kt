package com.notes.shared

import android.app.Application
import com.notes.shared.coreUi.ClipboardManagerAndroidImpl

object NotesAndroidDependenciesInitializer {

    fun init(context: Application) {
        NotesDependencies.init(
            dataStore = DataStoreAndroidImpl(dataStore = DataStoreProvider.get(context)),
            databasePasswordProvider = DatabasePasswordProviderAndroidImpl(dataStore = DataStoreProvider.get(context)),
            clipboardManager = ClipboardManagerAndroidImpl(context)
        )
    }

    fun clearData() {
        NotesDependencies.clearData()
    }
}