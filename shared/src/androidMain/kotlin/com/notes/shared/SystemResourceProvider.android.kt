package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenWidth(): Int {
    return with(LocalDensity.current){ LocalConfiguration.current.screenWidthDp.dp.toPx().toInt()}
}