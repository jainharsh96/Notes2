package com.notes.shared.db


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.notes.shared.NotesDependencies
import com.notes.shared.getDatabaseBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [Note::class, DeletedNote::class],
    exportSchema = true,
    version = 2,
    autoMigrations = arrayOf(AutoMigration(from = 1, to = 2))
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    companion object {
        const val DATABASE_FILE_NAME_V2 = "NotesDb2.db"

        private fun getDBPasscode() = NotesDependencies.databasePasswordProvider?.getPassword().orEmpty()

        private fun getDatabase() = getDatabaseBuilder(databaseName = DATABASE_FILE_NAME_V2, password = getDBPasscode())
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        var databaseObj : NotesDatabase? = null

        /*
        try to init DB with stored password and validate password is correct or not
         */
        suspend fun tryInitDb() : Boolean {
            if (isDBInitialized()) return true
            val db = runCatching {
                val db = getDatabase()
                db.notesDao().findNoteById(1) // to check whether DB is accessible or not with given password
                databaseObj = db
                databaseObj
            }.getOrNull()
            return db != null
        }

        fun isDBInitialized() = databaseObj != null

        suspend fun reInitDatabase() {
            if (isDBInitialized()){
                databaseObj?.close()
                databaseObj = null
            }
            tryInitDb()
        }
    }
}