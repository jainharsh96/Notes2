package com.notes.shared.ui.settingscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.NotesSyncManager
import com.notes.shared.coreUi.showToast
import com.notes.shared.painterResource
import com.notes.shared.ui.NavigationAction
import com.notes.shared.utils.colorResource
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.colorPrimaryDark
import notes2.shared.generated.resources.colorUpdate
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.ic_restore
import notes2.shared.generated.resources.white


@Composable
fun SettingScreenShared(onAction: (NavigationAction) -> Unit, notesSyncManager: NotesSyncManager?) {
    var syncData by remember { mutableStateOf(false) }
    var restoreData by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = syncData, key2 = restoreData){
        if (syncData){
            val result = notesSyncManager?.syncDataToCloud(isBgSync = false)
            syncData = false
            result?.getResultMsg()?.let { showToast(it) }
        } else if (restoreData){
            val result = notesSyncManager?.restoreDataFromCloud()
            restoreData = false
            result?.getResultMsg()?.let { showToast(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(Res.string.white))
            .statusBarsPadding()
    ) {
        SettingScreenHeader {
            onAction.invoke(NavigationAction.Back)
        }
        DraftNoteCard(
            openDraftNote = {
                onAction.invoke(NavigationAction.OpenDraftNote)
            }
        )
        if (notesSyncManager?.hasSupportSync() == true) {
            RestoreDataCard(
                restoreData = {
                    restoreData = true
                }
            )
            SyncDataCard(
                syncData = {
                    syncData = true
                }
            )
        }
    }
    if (syncData || restoreData) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // dim background
                .clickable(enabled = false) {},  // disable clicks
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RestoreDataCard(restoreData: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = restoreData)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_restore),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp),
            )
            Text(
                text = "Restore Data from Cloud",
                color = colorResource(Res.string.colorUpdate),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SyncDataCard(syncData: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = syncData)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_restore),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp),
            )
            Text(
                text = "Sync Data to Cloud",
                color = colorResource(Res.string.colorUpdate),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DraftNoteCard(openDraftNote: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { openDraftNote.invoke() }
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_restore),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp),
            )
            Text(
                text = "Drafted Notes",
                color = colorResource(Res.string.colorUpdate),
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AskScreenUnLockPassword(openDraftNote: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            .clickable { openDraftNote.invoke() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_restore),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp),
            )
            Text(
                text = "Drafted Notes",
                color = colorResource(Res.string.colorUpdate),
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
            contentDescription = "",
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .clickable(onClick = onClickBack),
        )
        Text(
            text = "Setting",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = colorResource(Res.string.colorPrimaryDark),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
    }
}