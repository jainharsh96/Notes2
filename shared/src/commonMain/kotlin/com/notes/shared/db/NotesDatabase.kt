package com.notes.shared.db


import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [Note::class, DeletedNote::class, Reminder::class],
    exportSchema = true,
    version = 3,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao

    abstract fun reminderDao() : ReminderDao
}


// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<NotesDatabase> {
    override fun initialize(): NotesDatabase
}