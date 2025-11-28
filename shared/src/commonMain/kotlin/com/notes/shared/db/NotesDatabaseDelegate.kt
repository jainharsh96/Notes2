package com.notes.shared.db

import androidx.room.RoomDatabase.JournalMode
import com.notes.shared.NotesDependencies
import com.notes.shared.getDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow

object NotesDatabaseDelegate{
    const val DATABASE_FILE_NAME_V2 = "NotesDb2.db"

    val flowTest = MutableStateFlow("")

    private suspend fun getDBPasscode() =
        NotesDependencies.databasePasswordProvider?.getPassword()

    private fun getNotesDatabase(dbPassword: String) =
        getDatabaseBuilder(databaseName = DATABASE_FILE_NAME_V2, password = dbPassword)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    var databaseObj: NotesDatabase? = null

    /*
    try to init DB with stored password and validate password is correct or not
     */
    suspend fun tryInitDb(): Boolean {
        if (isDBAlreadyInitialized()) return true
        val db = runCatching {
            val dbPassword = getDBPasscode()
            if (dbPassword.isNullOrEmpty()) return@runCatching null  // if password is not set, return null
            val db = getNotesDatabase(dbPassword = dbPassword)
            db.notesDao()
                .isDbAccessible() // to check whether DB is accessible or not with given password
            databaseObj = db
            databaseObj
        }.getOrNull()
        return db != null
    }

    fun isDBAlreadyInitialized() = databaseObj != null

    suspend fun reInitDatabase() {
        if (isDBAlreadyInitialized()) {
            databaseObj?.close()
            databaseObj = null
        }
        tryInitDb()
    }
}