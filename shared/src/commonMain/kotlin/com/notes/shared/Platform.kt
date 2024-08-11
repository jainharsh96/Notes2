package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.room.RoomDatabase
import com.notes.shared.db.NotesDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun setSystemBarColorAndIcon(color : Color, isDarkIcon : Boolean)

expect fun getDatabaseBuilder() : RoomDatabase.Builder<NotesDatabase>

