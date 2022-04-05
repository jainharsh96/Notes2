package com.harsh.notes.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.harsh.notes.models.Note
import com.harsh.notes.utils.DateConverter
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

@TypeConverters(DateConverter::class)
@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}

// todo fetch from external storage
object NotesDb {
    private const val DATABASE_NAME = "NotesDb.db"
    private const val DATABASE_PASSWORD = "thisispassword123!@#"

    private val sSQLiteDatabaseHook: SQLiteDatabaseHook = object : SQLiteDatabaseHook {
        override fun preKey(database: SQLiteDatabase) {}
        override fun postKey(database: SQLiteDatabase) {
//            database.rawExecSQL("PRAGMA cipher_compatibility = 3;")
//            database.rawExecSQL("PRAGMA cipher_page_size = 1024;")
//            database.rawExecSQL("PRAGMA kdf_iter = 64000;")
//            database.rawExecSQL("PRAGMA cipher_hmac_algorithm = HMAC_SHA1;")
//            database.rawExecSQL("PRAGMA cipher_kdf_algorithm = PBKDF2_HMAC_SHA1;")
        }
    }

    private val databaseSupportFactory: SupportFactory
        get() {
            val passPhrases = SQLiteDatabase.getBytes(DATABASE_PASSWORD.toCharArray())
            return SupportFactory(passPhrases, sSQLiteDatabaseHook, true)
        }

    fun getDatabase(context: Context) =
        Room.databaseBuilder(context, NotesDatabase::class.java, DATABASE_NAME)
            .openHelperFactory(databaseSupportFactory)
            .build()
}