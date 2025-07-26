package com.notes.shared.ui.notesscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.FractionalThreshold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.shared.painterResource
import com.notes.shared.ui.NavigationAction
import com.notes.shared.ui.NavigationAction.NavigateToCreateNoteScreen
import com.notes.shared.ui.uientity.NoteEntity
import com.notes.shared.utils.colorResource
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.add_notes
import notes2.shared.generated.resources.colorPrimaryDark
import notes2.shared.generated.resources.disable
import notes2.shared.generated.resources.ic_arrow_back_black_24dp
import notes2.shared.generated.resources.ic_setting
import notes2.shared.generated.resources.light_red
import notes2.shared.generated.resources.red
import notes2.shared.generated.resources.voice_note
import notes2.shared.generated.resources.white

@Composable
fun NotesScreenShared(
    state: NotesContract.State,
    effect: SharedFlow<NotesContract.SideEffect>,
    onAction: (NavigationAction) -> Unit,
    event: (NotesContract.Event) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        event(NotesContract.Event.FetchNotes)
    }
    LaunchedEffect(key1 = Unit) {
        effect.collect { sideEffect ->
            when (sideEffect) {
                NotesContract.SideEffect.AddNotes -> onAction.invoke(
                    NavigateToCreateNoteScreen(
                        noteId = null,
                        openRecording = false
                    )
                )

                NotesContract.SideEffect.ClickBack -> onAction.invoke(NavigationAction.Back)
                is NotesContract.SideEffect.OpenNote -> onAction.invoke(
                    NavigateToCreateNoteScreen(
                        noteId = sideEffect.noteId,
                        openRecording = false
                    )
                )

                NotesContract.SideEffect.OpenSettings -> onAction.invoke(NavigationAction.NavigateToSettingScreen)
                NotesContract.SideEffect.RecordNotes -> onAction.invoke(
                    NavigateToCreateNoteScreen(
                        noteId = null,
                        openRecording = true
                    )
                )

                NotesContract.SideEffect.GotoLockScreen -> onAction.invoke(NavigationAction.GotoLockScreen)
            }
        }
    }
    LaunchedEffect(state) {
        if (state.unLockAppFirst){
            onAction.invoke(NavigationAction.GotoLockScreen)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize().statusBarsPadding()
    ) {
        NotesContent(
            noteState = state,
            event = event
        )
        if (state.isDraftState.not()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    containerColor = colorResource(Res.string.colorPrimaryDark),
                    onClick = remember {
                        {
                            event(NotesContract.Event.RecordNotes)
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.voice_note),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                FloatingActionButton(
                    containerColor = colorResource(Res.string.colorPrimaryDark),
                    onClick = remember { { event(NotesContract.Event.AddNotes) } }) {
                    Image(
                        painter = painterResource(Res.drawable.add_notes),
                        contentDescription = ""
                    )
                }
            }
        }
    }
    if(state.alertDialogState != null){
        PasswordAlertDialog(
            errorMsg = state.alertDialogState.errorMsg,
            onConfirm = {
                event(NotesContract.Event.EnteredPassword(it))
            }
        )
    }
}

@Composable
fun PasswordAlertDialog(
    errorMsg : String?,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {  },
        title = { Text("Enter Password") },
        text = {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                singleLine = true,
                isError = errorMsg?.isNotEmpty() == true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.isNotEmpty()) {
                        onConfirm(password)
                    }
                }
            ) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesContent(
    noteState: NotesContract.State,
    event: (NotesContract.Event) -> Unit
) {
    val isDraftScreen = noteState.isDraftState
    Column(modifier = Modifier.fillMaxSize()) {
        NotesHeader(
            if (isDraftScreen) "Drafted Note" else "Notes ",
            isDraftScreen,
            event
        )
        if (noteState.notes.isNullOrEmpty()) {
            NoDataView()
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                NotesList(
                    isDraftScreen = isDraftScreen,
                    notes = noteState.notes,
                    event = event
                )
                if (noteState.confirmToDeleteNoteId != null) {
                    AlertDialog(
                        onDismissRequest = {
                            event.invoke(NotesContract.Event.DismissConfirmToDeleteNote)
                        },
                        title = {
                            Text(
                                text = "Are you sure, you want to delete this note ?",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        confirmButton = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    modifier = Modifier.width(100.dp),
                                    onClick = {
                                        event.invoke(
                                            NotesContract.Event.DeleteNote(
                                                noteState.confirmToDeleteNoteId
                                            )
                                        )
                                    }
                                ) {
                                    Text("Yes", style = TextStyle(fontSize = 20.sp))
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun NoDataView() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Notes...",
            color =  colorResource(Res.string.disable),
            style = TextStyle(fontSize = 24.sp),
        )
    }
}

@Composable
fun NotesHeader(heading: String, isDraftScreen: Boolean, event: (NotesContract.Event) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDraftScreen) {
            Image(
                painter = painterResource(Res.drawable.ic_arrow_back_black_24dp),
                contentDescription = "",
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .clickable { event.invoke(NotesContract.Event.ClickBack) },
            )
        }
        Text(
            text = heading,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = colorResource(Res.string.colorPrimaryDark),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        if (isDraftScreen.not()) {
            Image(
                painter = painterResource(Res.drawable.ic_setting),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { event.invoke(NotesContract.Event.OpenSettings) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NotesList(isDraftScreen: Boolean, notes: List<NoteEntity>, event: (NotesContract.Event) -> Unit) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        if (notes.isEmpty()) {
            item {
                Column(modifier = Modifier.fillParentMaxSize()) {
                    NoDataView()
                }
            }
        }
        items(items = notes, key = { it.id }) { note ->
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    when (it) {
                        DismissValue.DismissedToEnd -> if (isDraftScreen) event.invoke(
                            NotesContract.Event.RestoreNote(
                                noteId = note.id
                            )
                        ) else event.invoke(
                            NotesContract.Event.DraftNote(
                                noteId = note.id
                            )
                        )

                        DismissValue.DismissedToStart -> if (isDraftScreen) event(
                            NotesContract.Event.ConfirmDeleteNote(
                                noteId = note.id
                            )
                        )

                        else -> {}
                    }
                    true
                }
            )
            SwipeToDismiss(state = dismissState, directions = if (isDraftScreen) setOf(
                DismissDirection.StartToEnd,
                DismissDirection.EndToStart
            ) else setOf(
                DismissDirection.StartToEnd
            ), dismissThresholds = {
                FractionalThreshold(0.8f)
            }, background = {
                val alignment = when (dismissState.dismissDirection ?: return@SwipeToDismiss) {
                    DismissDirection.StartToEnd -> Alignment.CenterStart
                    DismissDirection.EndToStart -> Alignment.CenterEnd
                }
                val icon = when (dismissState.dismissDirection ?: return@SwipeToDismiss) {
                    DismissDirection.StartToEnd -> if (isDraftScreen) Icons.Default.Restore else Icons.Default.Drafts
                    DismissDirection.EndToStart -> Icons.Default.Delete
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colorResource(Res.string.light_red)),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tint = colorResource(Res.string.red)
                    )
                }
            }, dismissContent = {
                NoteHolder(note, event)
            })
        }
    }
}

@Composable
fun NoteHolder(note: NoteEntity, event: (NotesContract.Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { event.invoke(NotesContract.Event.OpenNote(note.id)) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(Res.string.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.firstLineData(),
                    modifier = Modifier.weight(1f),
                    color = colorResource(Res.string.colorPrimaryDark),
                    style = TextStyle(fontSize = 18.sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = note.updatedDate.orEmpty(),
                    color = colorResource(Res.string.disable),
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = note.secondLineData(),
                color = colorResource(Res.string.disable),
                style = TextStyle(fontSize = 14.sp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

//@MultiDevicePreview
//@Composable
//fun TestCompose() {
//    val fakeNotes = listOf(
//        Note(id = 1, body = "hellow 1", updatedDate = Date()),
//        Note(id = 2, body = "hellow 2", updatedDate = Date()),
//        Note(id = 3, body = "hellow 3", updatedDate = Date()),
//        Note(id = 4, body = "hellow 4", updatedDate = Date()),
//        Note(id = 5, body = "hellow 5", updatedDate = Date())
//    )
//    val mockState = NotesContract.State(isDraftState = false, notes = fakeNotes)
//    NotesScreen(
//        state = mockState,
//        effect = MutableSharedFlow(),
//        event = {},
//        onAction = {})
//}
