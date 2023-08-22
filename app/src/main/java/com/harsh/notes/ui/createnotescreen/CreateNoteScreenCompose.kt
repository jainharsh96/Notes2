package com.harsh.notes.ui.createnotescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.notes.R


@Composable
fun CreateNoteScreen(viewModel: CreateNoteViewModel) {
    val state by viewModel.state.collectAsState()
    val effect = viewModel.sideEffect    // TODO HANDLE THIS HERE
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ) {
        CreateNoteHeader(
            hasNote = state.hasNote(),
            event = viewModel::event
        )
        NoteInfo(state = state, event = viewModel::event)
    }
}

@Composable
fun CreateNoteHeader(hasNote: Boolean, event: (CreateNoteContract.Event) -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_back_black_24dp),
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
            color = colorResource(
                id = R.color.colorPrimaryDark
            ),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.voice_note),
            contentDescription = "",
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .clickable { event(CreateNoteContract.Event.ClickRecordNotes) },
            colorFilter = ColorFilter.tint(
                colorResource(id = R.color.colorPrimaryDark)
            )
        )
        if (hasNote) {
            Spacer(modifier = Modifier.padding(8.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_undo),
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
                backgroundColor = colorResource(id = if (state.enteredMsg.isEmpty()) R.color.disable else R.color.colorUpdate)
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_check_black_24dp),
                contentDescription = "", colorFilter = ColorFilter.tint(
                    colorResource(id = R.color.white)
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
                color = colorResource(id = R.color.disable),
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun TestCompose1() {
    CreateNoteHeader(hasNote = false, event = {})
}