package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDatabase
import com.notes.shared.db.instantiateImpl
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
actual fun getDatabaseBuilder(): RoomDatabase.Builder<NotesDatabase> {
    val DATABASE_NAME = "NotesDb2.db"

    val documentsDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: NSHomeDirectory()
    val dbFilePath = "$documentsDirectory/${DATABASE_NAME}"
    return Room.databaseBuilder<NotesDatabase>(
        name = dbFilePath,
        factory =  { NotesDatabase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver()).addMigrations()
}

@Composable
actual fun setSystemBarColorAndIcon(
    color: Color,
    isDarkIcon: Boolean
) {
    // TODO NOT IMPLEMENTED
}