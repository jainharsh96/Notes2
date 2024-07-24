package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun showToast(msg : String)

expect fun getDrawable(byName : String) : Any?

expect fun getDrawableId(byName : String) : Any?

expect fun getColor(name : String) : Color

@Composable
expect fun getPainter(resource: String) : Painter