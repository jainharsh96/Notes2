package com.notes.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.notes.shared.ui.notesscreen.NotesContract
import com.notes.shared.ui.notesscreen.NotesScreenShared
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.flow.MutableSharedFlow


@Composable
@Preview
fun NotesScreenPreview(){
    val mockNoteState = NotesContract.State(isDraftState = true, unLockAppFirst = false, notes = listOf(NoteEntity(id = 1, body = "test123", createdDate = "", updatedDate = "")))
    NotesScreenShared(state = mockNoteState, effect = MutableSharedFlow(), onAction = {}) {

    }
}

