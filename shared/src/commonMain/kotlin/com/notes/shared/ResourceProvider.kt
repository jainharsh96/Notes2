package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource


@Composable
fun painterResource(resource: DrawableResource): Painter {
    return if(LocalInspectionMode.current){
        ColorPainter(Color.Transparent)
    }else {
        org.jetbrains.compose.resources.painterResource(resource)
    }
}

@Composable
fun stringResource(resource: StringResource): String {
    return if(LocalInspectionMode.current){
        resource.key
    }else {
        org.jetbrains.compose.resources.stringResource(resource)
    }
}