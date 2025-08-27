package com.notes.shared.coreUi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.utils.colorResource
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.colorPrimaryDark


@Composable
fun TopBar(
    title: String,
    leadingIcons: @Composable (() -> Unit)? = null,
    trailingIcons: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
    ) {
        if (leadingIcons != null) {
            Box(modifier = Modifier) {
                leadingIcons()
            }
        }
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = colorResource(Res.string.colorPrimaryDark),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        if (trailingIcons != null) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                trailingIcons()
            }
        }
    }
}