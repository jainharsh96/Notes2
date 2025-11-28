package com.notes.shared

import androidx.room.RoomDatabase
import com.notes.shared.db.NotesDatabase

interface Platform {
    val name: PlatFormName

    enum class PlatFormName { ANDROID, IOS, JVM_DESKTOP }

    fun allowShowingDebugWindow() : Boolean
}

expect fun getPlatform(): Platform

expect fun getDatabaseBuilder(
    databaseName: String,
    password: String
): RoomDatabase.Builder<NotesDatabase>

