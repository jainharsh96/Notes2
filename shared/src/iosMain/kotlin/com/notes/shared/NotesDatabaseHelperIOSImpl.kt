package com.notes.shared

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.JournalMode
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.notes.shared.db.NotesDataBaseHelper
import com.notes.shared.db.NotesDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class NotesDatabaseHelperIOSImpl : NotesDataBaseHelper {

    @OptIn(ExperimentalForeignApi::class)
    fun getDatabaseBuilder(databaseName : String, password : String): RoomDatabase.Builder<NotesDatabase> {
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


    override suspend fun initDataBase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabaseBuilder(databaseName, password)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override suspend fun openDatabase(
        databaseName: String,
        password: String
    ): NotesDatabase? {
        return getDatabaseBuilder(databaseName, password)
            .setJournalMode(JournalMode.TRUNCATE)
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override suspend fun deleteDatabaseFile(databaseName: String): Boolean {
        return deleteDocumentFile(databaseName)
    }

    override suspend fun isDatabaseFilePresent(databaseName: String): Boolean {
        return isDatabaseFilePresentInternal(databaseName)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun deleteDocumentFile(fileName: String): Boolean {
        val fileManager = NSFileManager.defaultManager

        // 1. Locate the NSDocumentDirectory URL
        val documentDirectoryUrl: NSURL? = fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        ) ?: return false

        // 2. Append the target file name to the directory path
        val targetFileUrl: NSURL = documentDirectoryUrl?.URLByAppendingPathComponent(fileName) ?: return false
        val path = targetFileUrl.path ?: return false

        // 3. Verify file existence before attempting deletion
        if (!fileManager.fileExistsAtPath(path)) {
            return false
        }

        // 4. Remove item and capture errors via Kotlin/Native error bridging
        return fileManager.removeItemAtURL(targetFileUrl, error = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun isDatabaseFilePresentInternal(fileName: String): Boolean {
        val fileManager = NSFileManager.defaultManager

        // 1. Locate the NSDocumentDirectory URL
        val documentDirectoryUrl: NSURL? = fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        ) ?: return false

        // 2. Append the target file name to the directory path
        val targetFileUrl: NSURL = documentDirectoryUrl?.URLByAppendingPathComponent(fileName) ?: return false
        val path = targetFileUrl.path ?: return false

        // 3. Verify file existence before attempting deletion
        return fileManager.fileExistsAtPath(path)
    }
}