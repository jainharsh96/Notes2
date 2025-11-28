package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDatabase
import java.io.File


class JvmPlatform : Platform {
    override val name: Platform.PlatFormName = Platform.PlatFormName.JVM_DESKTOP
    override fun allowShowingDebugWindow(): Boolean {
        return true
    }
}

actual fun getPlatform(): Platform {
    return JvmPlatform()
}

actual fun getDatabaseBuilder(
    databaseName: String,
    password: String
): RoomDatabase.Builder<NotesDatabase> {
    // macOS Downloads directory
    val downloadsDir = File(System.getProperty("user.home"), "Downloads")

    val dbFile = File(downloadsDir, databaseName)
    return Room.databaseBuilder<NotesDatabase>(
       dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
}