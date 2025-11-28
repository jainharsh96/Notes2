package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDatabase


class JvmPlatform : Platform {
    override val name: String = "Jvm platform"
}

actual fun getPlatform(): Platform {
    return JvmPlatform()
}

actual fun getDatabaseBuilder(
    databaseName: String,
    password: String
): RoomDatabase.Builder<NotesDatabase> {
    return Room.databaseBuilder<NotesDatabase>(
       databaseName,
    ).setDriver(BundledSQLiteDriver())
}