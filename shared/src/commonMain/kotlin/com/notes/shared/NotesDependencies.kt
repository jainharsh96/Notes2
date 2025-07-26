package com.notes.shared

object NotesDependencies {
    var notesSyncManager : NotesSyncManager? = null
        private set
    var databasePasswordProvider : DatabasePasswordProvider? = null
        private set
    var dataStore : DataStore? = null
        private set

    fun init(
        notesSyncManager: NotesSyncManager? = null,
        databasePasswordProvider: DatabasePasswordProvider? = null,
        dataStore: DataStore? = null
    ){
        this.notesSyncManager = notesSyncManager
        this.databasePasswordProvider = databasePasswordProvider
        this.dataStore = dataStore
    }

    fun clearData(){
        notesSyncManager = null
        databasePasswordProvider = null
        dataStore = null
    }
}