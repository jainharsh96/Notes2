package com.notes.shared.coreUi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier


@Stable
enum class HapticFeedbackType {
    LIGHT, MEDIUM, HEAVY, LONG_PRESS, CONFIRM, REJECT
}


@Composable
expect fun Modifier.clickableWithHapticFeedback(
    type: HapticFeedbackType = HapticFeedbackType.LIGHT,
    onClick: () -> Unit
): Modifier

@Composable
expect fun performHapticFeedback(
    type: HapticFeedbackType,
)