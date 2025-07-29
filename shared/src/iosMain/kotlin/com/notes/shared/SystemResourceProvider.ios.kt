package com.notes.shared

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun getScreenWidth(): Int {
    return 1080 // todo UIScreen.mainScreen.bounds.size.width.toInt()
}