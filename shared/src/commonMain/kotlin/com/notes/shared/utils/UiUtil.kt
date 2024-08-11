package com.notes.shared.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.StringResource
import com.notes.shared.stringResource
import kotlinx.coroutines.delay


@Composable
fun colorResource(hex : StringResource) : Color = stringResource(hex).toColor()

private fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    val alpha: Int
    val red: Int
    val green: Int
    val blue: Int

    when (hex.length) {
        6 -> {
            alpha = 255
            red = hex.substring(0, 2).toInt(16)
            green = hex.substring(2, 4).toInt(16)
            blue = hex.substring(4, 6).toInt(16)
        }
        8 -> {
            alpha = hex.substring(0, 2).toInt(16)
            red = hex.substring(2, 4).toInt(16)
            green = hex.substring(4, 6).toInt(16)
            blue = hex.substring(6, 8).toInt(16)
        }
        else -> throw IllegalArgumentException("Invalid color format")
    }

    return Color(red, green, blue, alpha)
}