package com.harsh.notes.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.notes.shared.ui.notesscreen.NotesContract
import com.notes.shared.ui.notesscreen.NotesScreenShared
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.flow.MutableSharedFlow

@Preview(
    fontScale = 1.2f,
    showBackground = true,
    name = "small_device",
    device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480"
)
@Preview(fontScale = 0.8f, showBackground = true, name = "large_device")
annotation class MultiDevicePreview


@Composable
@MultiDevicePreview
fun NotesScreenPreview(){
    val mockNoteState = NotesContract.State(isDraftState = false, unLockAppFirst = false, notes = listOf(NoteEntity(id = 1, body = "test", createdDate = "", updatedDate = "")))
    NotesScreenShared(state = mockNoteState, effect = MutableSharedFlow(), onAction = {}) {
        
    }
}