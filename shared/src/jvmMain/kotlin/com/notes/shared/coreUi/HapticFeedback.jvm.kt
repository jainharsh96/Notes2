package com.notes.shared.coreUi

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.clickableWithHapticFeedback(
    type: HapticFeedbackType,
    onClick: () -> Unit
): Modifier {
    return Modifier.clickable(onClick = onClick)
}

@Composable
actual fun performHapticFeedback(type: HapticFeedbackType) {

}