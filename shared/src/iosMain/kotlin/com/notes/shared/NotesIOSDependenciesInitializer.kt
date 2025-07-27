package com.notes.shared

import com.notes.shared.di.NotesKoin

/*
call this function in the iOS AppDelegate to initialize dependencies.
 */
object NotesIOSDependenciesInitializer {
    fun init() {
        NotesKoin.init()
        NotesDependencies.init(
            dataStore = DataStoreIOSImpl(dataStore = DataStoreProvider.dataStore),
            databasePasswordProvider = DatabasePasswordProviderIOSImpl()
        )
    }
}