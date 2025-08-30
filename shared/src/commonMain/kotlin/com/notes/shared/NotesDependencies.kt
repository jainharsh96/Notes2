package com.notes.shared

import com.notes.shared.coreUi.ClipboardManager

object NotesDependencies {
    var notesSyncManager : NotesSyncManager? = null
        private set
    var databasePasswordProvider : DatabasePasswordProvider? = null
        private set
    var dataStore : DataStore? = null
        private set

    var clipboardManager : ClipboardManager? = null
        private set

    fun init(
        databasePasswordProvider: DatabasePasswordProvider? = null,
        dataStore: DataStore? = null,
        clipboardManager: ClipboardManager? = null
    ){
        this.databasePasswordProvider = databasePasswordProvider
        this.dataStore = dataStore
        this.clipboardManager = clipboardManager
    }

    fun initSyncManager(notesSyncManager: NotesSyncManager){
        this.notesSyncManager = notesSyncManager
    }

    fun clearData(){
        notesSyncManager = null
        databasePasswordProvider = null
        dataStore = null
        clipboardManager = null
    }
}