package com.notes.shared

import com.notes.shared.coreUi.ClipboardManager
import com.notes.shared.db.NotesDataBaseHelper
import com.notes.shared.di.NotesKoin

object NotesDependencies {
    var notesSyncManager : NotesSyncManager? = null
        private set
    var databasePasswordProvider : DatabasePasswordProvider? = null
        private set

    var notesDatabaseHelper : NotesDataBaseHelper? = null
        private set
    var dataStore : DataStore? = null
        private set

    var clipboardManager : ClipboardManager? = null
        private set

    var isDebugBuild : Boolean = true

    fun init(
        isDebugBuild: Boolean,
        databasePasswordProvider: DatabasePasswordProvider? = null,
        notesDatabaseHelper: NotesDataBaseHelper? = null,
        dataStore: DataStore? = null,
        clipboardManager: ClipboardManager? = null
    ){
        this.isDebugBuild = isDebugBuild
        this.databasePasswordProvider = databasePasswordProvider
        this.notesDatabaseHelper = notesDatabaseHelper
        this.dataStore = dataStore
        this.clipboardManager = clipboardManager
        NotesKoin.init()
    }

    fun initSyncManager(notesSyncManager: NotesSyncManager){
        this.notesSyncManager = notesSyncManager
    }

    fun clearData(){
        notesSyncManager = null
        databasePasswordProvider = null
        notesDatabaseHelper = null
        dataStore = null
        clipboardManager = null
    }
}