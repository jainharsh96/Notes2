package com.notes.shared

import androidx.room.RoomDatabase
import com.notes.shared.db.NotesDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun showToast(msg : String)

expect fun getDatabaseBuilder() : RoomDatabase.Builder<NotesDatabase>

