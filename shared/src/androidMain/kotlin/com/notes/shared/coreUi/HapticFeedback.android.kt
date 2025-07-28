package com.notes.shared.coreUi

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView

@Composable
actual fun Modifier.clickableWithHapticFeedback(
    type: HapticFeedbackType,
    onClick: () -> Unit
): Modifier {
    val view = LocalView.current
    return this.then(
        Modifier.clickable(
            onClick = {
                performHapticFeedback(type, view)
                onClick()
            }
        )
    )
}

fun performHapticFeedback(type: HapticFeedbackType, view: View) {
    when (type) {
        HapticFeedbackType.LIGHT, HapticFeedbackType.REJECT -> view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )

        HapticFeedbackType.MEDIUM, HapticFeedbackType.CONFIRM, HapticFeedbackType.LONG_PRESS -> view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )

        HapticFeedbackType.HEAVY -> view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}

@Composable
actual fun performHapticFeedback(type: HapticFeedbackType) {
    val view = LocalView.current
    performHapticFeedback(type,view)
}