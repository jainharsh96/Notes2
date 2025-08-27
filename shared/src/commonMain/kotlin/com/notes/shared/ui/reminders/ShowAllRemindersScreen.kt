package com.notes.shared.ui.reminders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.coreUi.TopBar
import com.notes.shared.painterResource
import com.notes.shared.ui.NavigationAction
import com.notes.shared.ui.notesscreen.NoDataView
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.utils.colorResource
import kotlinx.coroutines.flow.SharedFlow
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.add_notes
import notes2.shared.generated.resources.colorPrimaryDark
import notes2.shared.generated.resources.disable
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.white


@Composable
fun ShowAllReminderScreenShared(
    state: ShowAllReminderContract.State,
    effect: SharedFlow<ShowAllReminderContract.SideEffect>,
    onAction: (NavigationAction) -> Unit,
    event: (ShowAllReminderContract.Event) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        event(ShowAllReminderContract.Event.FetchAllReminders)
    }
    LaunchedEffect(key1 = Unit) {
        effect.collect { sideEffect ->
            when (sideEffect) {
                ShowAllReminderContract.SideEffect.ClickBack -> onAction(NavigationAction.Back)
                is ShowAllReminderContract.SideEffect.GotoAddEditReminder -> onAction(
                    NavigationAction.NavigateToAddEditReminderScreen(
                        noteId = sideEffect.noteId,
                        reminderId = sideEffect.reminderId
                    )
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = "Reminders",
                leadingIcons = {
                    Image(
                        painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
                        contentDescription = "",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable { onAction(NavigationAction.Back) },
                    )
                },
            )
            if (state.reminders.isNotEmpty()) {
                Reminders(
                    modifier = Modifier.weight(1f),
                    reminders = state.reminders,
                    event = event
                )
            } else {
                NoDataView("No Reminders, please add new")
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.BottomEnd),
            containerColor = colorResource(Res.string.colorPrimaryDark),
            onClick = remember {
                {
                    event(ShowAllReminderContract.Event.ClickAddNewReminder)
                }
            }) {
            Image(
                painter = painterResource(Res.drawable.add_notes),
                contentDescription = ""
            )
        }
    }

    if (state.isLoading) {
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
fun Reminders(
    modifier: Modifier,
    reminders: List<ReminderEntity>,
    event: (ShowAllReminderContract.Event) -> Unit
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 80.dp)) {
        items(reminders) { reminder ->
            ReminderItem(
                modifier = Modifier,
                reminder = reminder,
                onClickReminder = { event(ShowAllReminderContract.Event.OnClickReminder(reminder)) },
                onToggleSwich = { event(ShowAllReminderContract.Event.OnToggleSwitch(reminder, it)) }
            )
        }
    }
}

@Composable
fun ReminderItem(
    modifier: Modifier = Modifier,
    reminder: ReminderEntity,
    onClickReminder: () -> Unit,
    onToggleSwich: (Boolean) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickReminder)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(end = 8.dp).widthIn(max = 80.dp),
                text = reminder.getRemindAtTime(),
                color = colorResource(Res.string.disable),
                style = TextStyle(fontSize = 12.sp),
                maxLines = 2
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.firstLineData(),
                    color = colorResource(Res.string.colorPrimaryDark),
                    style = TextStyle(fontSize = 18.sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = reminder.secondLineData(),
                    color = colorResource(Res.string.disable),
                    style = TextStyle(fontSize = 14.sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Switch(
                modifier = Modifier.padding(start = 8.dp),
                checked = reminder.isReminderSet(),
                onCheckedChange = onToggleSwich,
            )
        }
    }
}