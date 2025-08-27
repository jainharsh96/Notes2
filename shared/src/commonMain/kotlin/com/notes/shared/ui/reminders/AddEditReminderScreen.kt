package com.notes.shared.ui.reminders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.coreUi.TopBar
import com.notes.shared.painterResource
import com.notes.shared.ui.NavigationAction
import com.notes.shared.ui.createnotescreen.SetHint
import com.notes.shared.utils.colorResource
import kotlinx.coroutines.flow.SharedFlow
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.colorUpdate
import notes2.shared.generated.resources.disable
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.ic_check_black_24dp
import notes2.shared.generated.resources.white


@Composable
fun AddEditReminderScreenShared(
    state: AddEditReminderContract.State,
    effect: SharedFlow<AddEditReminderContract.SideEffect>,
    onAction: (NavigationAction) -> Unit,
    event: (AddEditReminderContract.Event) -> Unit
) {

    val allowToSave = state.allowToSave()
    LaunchedEffect(key1 = Unit) {
        event(AddEditReminderContract.Event.LoadReminder)
    }
    LaunchedEffect(key1 = Unit) {
        effect.collect { sideEffect ->
            when (sideEffect) {
                AddEditReminderContract.SideEffect.ClickBack -> onAction(NavigationAction.Back)
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
                title = state.getTitle(),
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
            BasicTextField(
                value = state.reminderEntity?.title.orEmpty(),
                onValueChange = { newVal ->
                    event(AddEditReminderContract.Event.OnTypeTitle(newVal))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                decorationBox = { innerTextField ->
                    SetHint(
                        hint = "Give reminder title and description in next lines",
                        showHint = state.reminderEntity?.title?.isEmpty() == true
                    )
                    innerTextField()
                }
            )

            // todo pick date time here
            Button(
                onClick = { event(AddEditReminderContract.Event.SaveReminder) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                enabled = allowToSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(if (allowToSave.not()) Res.string.disable else Res.string.colorUpdate)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_check_black_24dp),
                    contentDescription = "", colorFilter = ColorFilter.tint(
                        colorResource(Res.string.white)
                    )
                )
            }
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