package com.harsh.notes.ui.notesscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.harsh.Notes.NoteUtils.formated
import com.harsh.notes.R
import com.harsh.notes.db.Note
import com.harsh.notes.ui.NavigationAction
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun NotesScreen(viewModel: NotesViewModel = hiltViewModel(), onAction: (NavigationAction) -> Unit) {
    val state by viewModel.state.collectAsState()
    val effect = viewModel.sideEffect
    LaunchedEffect(key1 = Unit) {
        viewModel.event(NotesContract.Event.FetchNotes(if (viewModel.isDraftScreen) Note.DRAFTED else Note.SAVED))
    }
    LaunchedEffect(key1 = Unit) {
        effect.collectLatest { sideEffect ->
            when (sideEffect) {
                NotesContract.SideEffect.AddNotes -> onAction.invoke(
                    NavigationAction.NavigateToCreateNoteScreen(
                        noteId = null,
                        openRecording = false
                    )
                )

                NotesContract.SideEffect.ClickBack -> onAction.invoke(NavigationAction.Back)
                is NotesContract.SideEffect.OpenNote -> onAction.invoke(
                    NavigationAction.NavigateToCreateNoteScreen(
                        noteId = sideEffect.noteId,
                        openRecording = false
                    )
                )

                NotesContract.SideEffect.OpenSettings -> onAction.invoke(NavigationAction.NavigateToSettingScreen)
                NotesContract.SideEffect.RecordNotes -> onAction.invoke(
                    NavigationAction.NavigateToCreateNoteScreen(
                        noteId = null,
                        openRecording = true
                    )
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotesContent(
            noteState = state,
            isDraftScreen = viewModel.isDraftScreen,
            event = remember(viewModel) {
                viewModel::event
            }
        )
        if (viewModel.isDraftScreen.not()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    backgroundColor = colorResource(id = R.color.colorPrimaryDark),
                    onClick = remember {
                        {
                            viewModel.event(NotesContract.Event.RecordNotes)
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.voice_note),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                FloatingActionButton(
                    backgroundColor = colorResource(id = R.color.colorPrimaryDark),
                    onClick = remember {{ viewModel.event(NotesContract.Event.AddNotes) }}) {
                    Image(
                        painter = painterResource(id = R.drawable.add_notes),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
fun NotesContent(
    noteState: NotesContract.State,
    isDraftScreen: Boolean,
    event: (NotesContract.Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        NotesHeader(
            if (isDraftScreen) "Drafted Note" else "Notes",
            isDraftScreen,
            event
        )
        when (noteState) {
            is NotesContract.State.NoData -> NoDataView()
            is NotesContract.State.Notes -> {
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
                            buttons = {
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
                            }
                        )
                    }
                }
            }

            else -> Unit
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
            color = colorResource(
                id = R.color.disable
            ),
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
                painter = painterResource(id = R.drawable.ic_arrow_back_black_24dp),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { event.invoke(NotesContract.Event.ClickBack) },
                alpha = 0.5f
            )
        }
        Text(
            text = heading,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = colorResource(
                id = R.color.colorPrimaryDark
            ),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold
        )
        if (isDraftScreen.not()) {
            Image(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { event.invoke(NotesContract.Event.OpenSettings) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesList(isDraftScreen: Boolean, notes: List<Note>, event: (NotesContract.Event) -> Unit) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = notes) {
        if (notes.isNotEmpty())
            listState.scrollToItem(0)
    }
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
            ), dismissThresholds = { direction ->
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
                        .background(color = colorResource(id = R.color.light_red)),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tint = colorResource(
                            id = R.color.red
                        )
                    )
                }
            }) {
                NoteHolder(note, event)
            }
        }
    }
}

@Composable
fun NoteHolder(note: Note, event: (NotesContract.Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { event.invoke(NotesContract.Event.OpenNote(note.id)) },
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.firstLineData(),
                    modifier = Modifier.weight(1f),
                    color = colorResource(id = R.color.colorPrimaryDark),
                    style = TextStyle(fontSize = 18.sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = note.updatedDate?.formated() ?: "",
                    color = colorResource(id = R.color.disable),
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = note.secondLineData(),
                color = colorResource(id = R.color.disable),
                style = TextStyle(fontSize = 14.sp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TestCompose() {
    NoteHolder(Note(body = "harsh notes", updatedDate = Date(System.currentTimeMillis())), {})
}
