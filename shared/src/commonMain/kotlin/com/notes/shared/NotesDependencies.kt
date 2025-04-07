package com.notes.shared

object NotesDependencies {
    var notesSyncManager : NotesSyncManager? = null
        private set
    var databasePasswordProvider : DatabasePasswordProvider? = null
        private set

    fun init(
        notesSyncManager: NotesSyncManager? = null,
        databasePasswordProvider: DatabasePasswordProvider? = null
    ){
        this.notesSyncManager = notesSyncManager
        this.databasePasswordProvider = databasePasswordProvider
    }

    fun clearData(){
        notesSyncManager = null
        databasePasswordProvider = null
    }
}