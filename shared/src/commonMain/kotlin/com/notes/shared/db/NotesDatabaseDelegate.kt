package com.notes.shared.db

import com.notes.shared.NotesDependencies

object NotesDatabaseDelegate {
    const val DATABASE_FILE_NAME_V2 = "NotesDb2.db"

    private suspend fun getDBPasscode() =
        NotesDependencies.databasePasswordProvider?.getPassword()

    private val dataBaseHelper
    get() = NotesDependencies.notesDatabaseHelper

    private var databaseObj: NotesDatabase? = null

    fun getDatabase() : NotesDatabase? {
        return databaseObj
    }

    suspend fun isDatabaseFilePresent() : Boolean {
        return dataBaseHelper?.isDatabaseFilePresent(DATABASE_FILE_NAME_V2) ?: false
    }

    suspend fun deleteDatabase(): Boolean {
        databaseObj?.close()
        databaseObj = null
        val isDeleted = dataBaseHelper?.deleteDatabaseFile(DATABASE_FILE_NAME_V2) ?: false
        return isDeleted
    }

    /*
    open database only if password and database file is present, else return false
     */
    suspend fun openDataBase(passcode : String) : Boolean {
        if (databaseObj != null && isDbAccessible(databaseObj!!)) return true

        if (passcode.isEmpty()) return false
        if (dataBaseHelper?.isDatabaseFilePresent(DATABASE_FILE_NAME_V2) == false) return false
        databaseObj = dataBaseHelper?.openDatabase(DATABASE_FILE_NAME_V2, passcode)
        return databaseObj != null && isDbAccessible(databaseObj!!)
    }

    /*
    setup new database with given password, and delete existing database if present
     */
    suspend fun setupNewDataBase(password: String) : Boolean {
        databaseObj?.close()
        deleteDatabase()
        NotesDependencies.databasePasswordProvider?.setPassword("")
        databaseObj = dataBaseHelper?.initDataBase(DATABASE_FILE_NAME_V2, password)
        return databaseObj != null
    }

    fun closeDatabase() {
        databaseObj?.close()
        databaseObj = null
    }

    private suspend fun isDbAccessible(db: NotesDatabase): Boolean {
        return runCatching {
            db.notesDao().isDbAccessible()
            true
        }.getOrDefault(false)
    }

    /*
    try to init DB with stored password and validate password is correct or not
     */
    suspend fun tryToOpenDatabase(): Boolean {
        return runCatching {
            val dbPassword = getDBPasscode() ?: return false
            openDataBase(dbPassword)
        }.getOrDefault(false)
    }

    suspend fun reOpenDatabase() : Boolean {
        closeDatabase()
        return tryToOpenDatabase()
    }
}

interface NotesDataBaseHelper {
    suspend fun initDataBase(
        databaseName: String,
        password: String
    ): NotesDatabase?

    suspend fun openDatabase(
        databaseName: String,
        password: String
    ): NotesDatabase?

    suspend fun deleteDatabaseFile(databaseName: String): Boolean

    suspend fun isDatabaseFilePresent(databaseName: String): Boolean
}