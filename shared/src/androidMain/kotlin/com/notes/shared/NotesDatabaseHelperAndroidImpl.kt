package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode
import com.notes.shared.db.NotesDataBaseHelper
import com.notes.shared.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory
import java.io.File

class NotesDatabaseHelperAndroidImpl : NotesDataBaseHelper {

    fun getDatabaseBuilder(databaseName : String, password : String): RoomDatabase.Builder<NotesDatabase> {
        val appContext = AndroidApplication.context
        val dbFile = getDatabasePath(databaseName)

        val sSQLiteDatabaseHook: SQLiteDatabaseHook = object : SQLiteDatabaseHook {
            override fun preKey(database: SQLiteDatabase) {}
            override fun postKey(database: SQLiteDatabase) {
                // can remove this
                database.rawExecSQL("PRAGMA journal_mode=DELETE")
            }
        }

        val passPhrases = SQLiteDatabase.getBytes(password.toCharArray())
        val databaseSupportFactory = SupportFactory(passPhrases, sSQLiteDatabaseHook, true)

        return Room.databaseBuilder<NotesDatabase>(
            context = appContext,
            name = dbFile
        ).openHelperFactory(databaseSupportFactory)
    }


    private fun getDatabase(databaseName: String, password: String): NotesDatabase {
        return getDatabaseBuilder(databaseName, password)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override suspend fun initDataBase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabase(databaseName, password)
    }

    override suspend fun openDatabase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabase(databaseName, password)
    }

    override suspend fun deleteDatabaseFile(databaseName: String): Boolean {
        return runCatching { File(getDatabasePath(databaseName)).delete() }.getOrDefault(false)
    }

    override suspend fun isDatabaseFilePresent(databaseName: String): Boolean {
        return runCatching { File(getDatabasePath(databaseName)).exists() }.getOrDefault(false)
    }
}