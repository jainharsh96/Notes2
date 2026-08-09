package com.notes.shared.ui.createnotescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.coreUi.SecureBasicTextField
import com.notes.shared.coreUi.secureKeyboard.KeyboardType
import com.notes.shared.coreUi.secureKeyboard.secureKeyBoardPadding
import com.notes.shared.painterResource
import com.notes.shared.ui.NavigationAction
import com.notes.shared.utils.colorResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.colorActionButton
import notes2.shared.generated.resources.colorPrimaryDark
import notes2.shared.generated.resources.colorUpdate
import notes2.shared.generated.resources.disable
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.ic_check_black_24dp
import notes2.shared.generated.resources.ic_undo
import notes2.shared.generated.resources.keyboard
import notes2.shared.generated.resources.voice_note
import notes2.shared.generated.resources.white
import org.jetbrains.compose.resources.ExperimentalResourceApi


@Composable
fun CreateNoteScreenShared(
    state: CreateNoteContract.State,
    effect: SharedFlow<CreateNoteContract.SideEffect>,
    event: (CreateNoteContract.Event) -> Unit,
    onAction: (NavigationAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
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
                is CreateNoteContract.SideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = sideEffect.msg,
                    )
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(Res.string.white))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .secureKeyBoardPadding()
    ) {
        CreateNoteHeader(
            hasNote = state.hasNote(),
            isShowingSystemKeyBoard = state.showSystemKeyboard,
            event = event
        )
        NoteInfo(state = state, event = event)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreateNoteHeader(hasNote: Boolean, isShowingSystemKeyBoard : Boolean, event: (CreateNoteContract.Event) -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
            contentDescription = "",
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .clickable { event(CreateNoteContract.Event.ClickBack) }
        )
        Text(
            text = if (hasNote) "Edit note" else "Add note",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = colorResource(Res.string.colorPrimaryDark),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
        )
        Icon(
            painter = painterResource(Res.drawable.voice_note),
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickable { event(CreateNoteContract.Event.ClickRecordNotes) }
                .padding(8.dp),
            tint = colorResource(Res.string.colorPrimaryDark),
        )
        if (hasNote) {
            Spacer(modifier = Modifier.padding(8.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_undo),
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { event(CreateNoteContract.Event.ClickUndo) }
                    .padding(8.dp),
                tint = colorResource(Res.string.colorPrimaryDark),
            )
        }
        Icon(
            painter = painterResource(Res.drawable.keyboard),
            contentDescription = "",
            modifier = Modifier.padding(8.dp)
                .size(40.dp)
                .clickable { event(CreateNoteContract.Event.ClickChangeKeyboard) }
                .padding(8.dp)
            ,
            tint = colorResource(if (isShowingSystemKeyBoard) Res.string.colorPrimaryDark else Res.string.colorActionButton),
        )
    }
}

@Composable
fun NoteInfo(state: CreateNoteContract.State, event: (CreateNoteContract.Event) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SecureBasicTextField(
            useSecureKeyBoard = state.showSystemKeyboard.not(),
            value = state.enteredMsg,
            onValueChange = { newVal ->
                event.invoke(CreateNoteContract.Event.OnType(newVal))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            textStyle = TextStyle(fontSize = 20.sp),
            decorationBox = { innerTextField ->
                SetHint(hint = "Write Note", showHint = state.enteredMsg.text.isEmpty())
                innerTextField()
            },
            keyboardType = KeyboardType.AlphaNumeric,
        )

        Button(
            onClick = { event(CreateNoteContract.Event.SaveNote) },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            enabled = state.enteredMsg.text.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(if(state.enteredMsg.text.isEmpty()) Res.string.disable else Res.string.colorUpdate)
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
                color = colorResource(Res.string.disable),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
@Preview
private fun PreviewCreateNoteScreenShared() {
    CreateNoteScreenShared(
        state = CreateNoteContract.State(
            enteredMsg = TextFieldValue(""),
            showSystemKeyboard = false
        ),
        effect = MutableSharedFlow(),
        event = {},
        onAction = {}
    )
}