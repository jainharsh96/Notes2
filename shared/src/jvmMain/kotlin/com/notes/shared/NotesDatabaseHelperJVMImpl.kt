package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDataBaseHelper
import com.notes.shared.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import java.io.File

class NotesDatabaseHelperJVMImpl : NotesDataBaseHelper {

    companion object {
        const val DATABASE_DIRECTORY = "Downloads"
    }
    private fun getDatabaseFileDirectory(): File {
        // database directory
        return File(System.getProperty("user.home"), DATABASE_DIRECTORY)
    }

    fun getDatabaseBuilder(
        databaseName: String,
        password: String
    ): RoomDatabase.Builder<NotesDatabase> {
        // Downloads directory
        val downloadsDir = getDatabaseFileDirectory()

        val dbFile = File(downloadsDir, databaseName)
        return Room.databaseBuilder<NotesDatabase>(
            dbFile.absolutePath,
        ).setDriver(BundledSQLiteDriver())
    }


    override suspend fun initDataBase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabaseBuilder(databaseName, password)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override suspend fun openDatabase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabaseBuilder(databaseName, password)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override suspend fun deleteDatabaseFile(databaseName: String): Boolean {
        val dbFile = File(getDatabaseFileDirectory(), databaseName)
        return if (dbFile.exists()) {
            dbFile.delete()
        } else {
            false
        }
    }

    override suspend fun isDatabaseFilePresent(databaseName: String): Boolean {
        val dbFile = File(getDatabaseFileDirectory(), databaseName)
        return dbFile.exists()
    }
}