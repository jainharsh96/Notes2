package com.notes.shared.coreUi

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

@Composable
actual fun Modifier.clickableWithHapticFeedback(
    type: HapticFeedbackType,
    onClick: () -> Unit
): Modifier {
    return this.then(
        Modifier.clickable(
            onClick = {
                performHapticFeedbackInternal(type)
                onClick()
            }
        )
    )
}

fun performHapticFeedbackInternal(type: HapticFeedbackType) {
    when (type) {
        HapticFeedbackType.LIGHT, HapticFeedbackType.REJECT -> {
            val lightImpact =
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
            lightImpact.prepare()
            lightImpact.impactOccurred()
        }

        HapticFeedbackType.MEDIUM, HapticFeedbackType.CONFIRM, HapticFeedbackType.LONG_PRESS -> {
            val mediumImpact =
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
            mediumImpact.prepare()
            mediumImpact.impactOccurred()
        }

        HapticFeedbackType.HEAVY -> {
            val heavyImpact =
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
            heavyImpact.prepare()
            heavyImpact.impactOccurred()
        }
    }
}

@Composable
actual fun performHapticFeedback(type: HapticFeedbackType) {
    performHapticFeedbackInternal(type)
}