package com.notes.shared

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.room.Room
import androidx.room.RoomDatabase
import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.NotesDatabaseDelegate
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

object AndroidApplication {
    lateinit var context: Application
}

//const val DATABASE_PASSWORD = "thisispassword123!@#"


class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

fun setApplicationContext(appContext: Context){
    AndroidApplication.context = appContext.applicationContext as Application
}

fun getDatabasePath() = Environment.getExternalStorageDirectory().absolutePath + "/Notes2/${NotesDatabaseDelegate.DATABASE_FILE_NAME_V2}"

actual fun getDatabaseBuilder(databaseName : String, password : String): RoomDatabase.Builder<NotesDatabase> {
    val appContext = AndroidApplication.context
    val dbFile = getDatabasePath()

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