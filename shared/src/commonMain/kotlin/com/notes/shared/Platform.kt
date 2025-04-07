package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.RoomDatabase
import com.notes.shared.db.NotesDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getDatabaseBuilder(databaseName : String, password : String) : RoomDatabase.Builder<NotesDatabase>

