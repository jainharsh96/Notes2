package com.notes.shared.coreUi

import androidx.compose.foundation.LocalIndication
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.notes.shared.utils.colorResource
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.colorPrimaryDark

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val rippleColor = colorResource(Res.string.colorPrimaryDark)
    val customRipple = remember {
        ripple(
            bounded = true,
            color = rippleColor
        )
    }

    val rippleConfiguration = remember {
        RippleConfiguration(
            rippleAlpha = RippleAlpha(
                pressedAlpha = 0.25f,
                focusedAlpha = 0.25f,
                draggedAlpha = 0.25f,
                hoveredAlpha = 0.25f
            )
        )
    }

    CompositionLocalProvider(
        LocalIndication provides customRipple,
        LocalRippleConfiguration provides rippleConfiguration
    ) {
        content()
    }
}