package com.notes.shared.coreUi

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Creates an SP value that ignores the system font scaling preference.
 */
val Int.nonScaledSp: TextUnit
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp