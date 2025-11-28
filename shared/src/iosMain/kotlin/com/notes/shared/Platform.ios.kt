package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(databaseName : String, password : String): RoomDatabase.Builder<NotesDatabase> {
    val documentsDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: NSHomeDirectory()
    val dbFilePath = "$documentsDirectory/${databaseName}"
    return Room.databaseBuilder<NotesDatabase>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver()).addMigrations()
}