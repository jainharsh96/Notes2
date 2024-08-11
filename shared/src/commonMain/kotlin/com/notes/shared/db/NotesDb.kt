package com.notes.shared.db


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
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
        fun getDatabase() = getDatabaseBuilder()
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}