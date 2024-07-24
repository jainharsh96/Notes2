package com.notes.shared.ui.settingscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.getColor
import com.notes.shared.getPainter
import com.notes.shared.ui.NavigationAction


@Composable
fun SettingScreenShared(onAction: (NavigationAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getColor("white"))
    ) {
        SettingScreenHeader {
            onAction.invoke(NavigationAction.Back)
        }
        RestoreDataCard(restoreData = {
            onAction.invoke(NavigationAction.RestoreData)
        }, onClickBack = {
            onAction.invoke(NavigationAction.Back)
        })
        DraftNoteCard(openDraftNote = {
            onAction.invoke(NavigationAction.OpenDraftNote)
        }, onClickBack = {
            onAction.invoke(NavigationAction.Back)
        })
    }
}

@Composable
fun RestoreDataCard(restoreData: () -> Unit, onClickBack: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            .clickable(onClick = restoreData),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = getPainter("ic_restore"),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onClickBack),
                alpha = 0.4f
            )
            Text(
                text = "Restore Data",
                color = getColor("colorUpdate"),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DraftNoteCard(openDraftNote: () -> Unit, onClickBack: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            .clickable { openDraftNote.invoke() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = getPainter("ic_restore"),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onClickBack),
                alpha = 0.4f
            )
            Text(
                text = "Drafted Notes",
                color = getColor("colorUpdate"),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingScreenHeader(onClickBack: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = getPainter("ic_arrow_back_black_24dp"),
            contentDescription = "",
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .clickable(onClick = onClickBack),
            alpha = 0.5f
        )
        Text(
            text = "Setting",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = getColor("colorPrimaryDark"),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
    }
}