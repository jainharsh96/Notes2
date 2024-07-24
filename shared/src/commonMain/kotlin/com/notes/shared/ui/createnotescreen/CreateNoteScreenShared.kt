package com.notes.shared.ui.createnotescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.getColor
import com.notes.shared.getPainter
import com.notes.shared.showToast
import com.notes.shared.ui.NavigationAction
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi


@Composable
fun CreateNoteScreenShared(
    state: CreateNoteContract.State,
    effect: SharedFlow<CreateNoteContract.SideEffect>,
    event: (CreateNoteContract.Event) -> Unit,
    onAction: (NavigationAction) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        event(CreateNoteContract.Event.FetchNote)
    }
    LaunchedEffect(key1 = Unit) {
        effect.collectLatest { sideEffect ->
            when (sideEffect) {
                CreateNoteContract.SideEffect.ClickBack -> onAction.invoke(NavigationAction.Back)
                CreateNoteContract.SideEffect.StartRecordNotes -> {
                    onAction(NavigationAction.RecordNotes)
                }

                CreateNoteContract.SideEffect.SavedNote -> onAction.invoke(NavigationAction.Back)
                is CreateNoteContract.SideEffect.ShowError -> showToast(sideEffect.msg)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getColor("white"))
    ) {
        CreateNoteHeader(
            hasNote = state.hasNote(),
            event = event
        )
        NoteInfo(state = state, event = event)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreateNoteHeader(hasNote: Boolean, event: (CreateNoteContract.Event) -> Unit) {
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
                .clickable { event(CreateNoteContract.Event.ClickBack) },
            alpha = 0.5f
        )
        Text(
            text = if (hasNote) "Edit note" else "Add note",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = getColor("colorPrimaryDark"),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = getPainter("voice_note"),
            contentDescription = "",
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .clickable { event(CreateNoteContract.Event.ClickRecordNotes) },
            colorFilter = ColorFilter.tint(
                getColor("colorPrimaryDark")
            )
        )
        if (hasNote) {
            Spacer(modifier = Modifier.padding(8.dp))
            Image(
                painter = getPainter("ic_undo"),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { event(CreateNoteContract.Event.ClickUndo) }
            )
        }
    }
}

@Composable
fun NoteInfo(state: CreateNoteContract.State, event: (CreateNoteContract.Event) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val focusRequester = remember { FocusRequester() }
        BasicTextField(
            value = state.enteredMsg,
            onValueChange = { newVal ->
                event.invoke(CreateNoteContract.Event.OnType(newVal))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .focusRequester(focusRequester),
            textStyle = TextStyle(fontSize = 20.sp),
            decorationBox = { innerTextField ->
                SetHint(hint = "Write Note", showHint = state.enteredMsg.isEmpty())
                innerTextField()
            }
        )
        if (state.hasNote().not()) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

        Button(
            onClick = { event(CreateNoteContract.Event.SaveNote) },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = state.enteredMsg.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = getColor(if (state.enteredMsg.isEmpty()) "disable" else "colorUpdate")
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = getPainter("ic_check_black_24dp"),
                contentDescription = "", colorFilter = ColorFilter.tint(
                    getColor("white")
                )
            )
        }
    }
}

@Composable
fun SetHint(hint: String, showHint: Boolean) {
    if (showHint) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .background(Color.Transparent),
                text = hint,
                color = getColor("disable"),
                fontSize = 20.sp
            )
        }
    }
}