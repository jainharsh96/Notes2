package com.harsh.notes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
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
import com.example.harsh.Notes.NoteUtils.formated
import com.harsh.notes.R
import com.harsh.notes.models.Note
import com.harsh.notes.models.NotesAction
import com.harsh.notes.models.NotesState
import com.harsh.notes.ui.notesscreen.NotesViewModel
import java.util.*

@Composable
fun TestCompose() {
    NoteHolder(Note(body = "harsh notes", date = Date(System.currentTimeMillis())), {})
}

@Composable
fun NotesScreen(viewModel: NotesViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotesContent(viewModel = viewModel)
        if (viewModel.isDraftScreen.not()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    backgroundColor = colorResource(id = R.color.colorPrimaryDark),
                    onClick = { viewModel.handleAction(NotesAction.RecordNotes) }) {
                    Image(
                        painter = painterResource(id = R.drawable.voice_note),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                FloatingActionButton(
                    backgroundColor = colorResource(id = R.color.colorPrimaryDark),
                    onClick = { viewModel.handleAction(NotesAction.AddNotes) }) {
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
fun NotesContent(viewModel: NotesViewModel) {
    val noteState = viewModel.notesState
    Column(modifier = Modifier.fillMaxSize()) {
        NotesHeader(
            if (viewModel.isDraftScreen) "Drafted Note" else "Notes",
            viewModel.isDraftScreen,
            viewModel.handleAction
        )
        when (noteState) {
            is NotesState.NoData -> NoDataView()
            is NotesState.Notes -> NotesList(
                isDraftScreen = viewModel.isDraftScreen,
                notes = noteState.notes,
                handleAction = viewModel.handleAction
            )
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
fun NotesHeader(heading: String, isDraftScreen: Boolean, handleAction: (NotesAction) -> Unit) {
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
                    .clickable { handleAction(NotesAction.ClickBack) },
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
                    .clickable { handleAction(NotesAction.OpenSettings) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesList(isDraftScreen: Boolean, notes: List<Note>, handleAction: (NotesAction) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp)
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
                        DismissValue.DismissedToEnd -> if (isDraftScreen) handleAction(
                            NotesAction.RestoreNote(
                                noteId = note.id
                            )
                        ) else handleAction(
                            NotesAction.DraftNote(
                                noteId = note.id
                            )
                        )
                        DismissValue.DismissedToStart -> if (isDraftScreen) handleAction(
                            NotesAction.DeleteNote(
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
                        .fillMaxSize(),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = "", modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }) {
                NoteHolder(note, handleAction)
            }
        }
    }
}

@Composable
fun NoteHolder(note: Note, handleAction: (NotesAction) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { handleAction(NotesAction.OpenNote(note.id)) },
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
                    text = note.date?.formated() ?: "",
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
                maxLines = 2
            )
        }
    }
}
