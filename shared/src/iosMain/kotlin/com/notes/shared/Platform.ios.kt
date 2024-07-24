package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun showToast(msg: String) {
    // TODO IMPLEMENT
}

actual fun getDrawable(byName: String): Any?{
    TODO("Not yet implemented")
}

actual fun getDrawableId(byName: String): Any? {
    TODO("Not yet implemented")
}

actual fun getColor(name: String): Color {
    TODO("Not yet implemented")
}

@Composable
actual fun getPainter(resource: String): Painter {
    TODO("Not yet implemented")
}