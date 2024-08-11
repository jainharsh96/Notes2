package com.notes.shared

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.shared.ui.notesscreen.NotesContract
import com.notes.shared.ui.notesscreen.NotesScreenShared
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import notes2.shared.generated.resources.Res
import notes2.shared.generated.resources.ic_setting


@Composable
@Preview
fun NotesScreenPreview(){
    val mockNoteState = NotesContract.State(isDraftState = true, notes = listOf(NoteEntity(id = 1, body = "test123", createdDate = "", updatedDate = "")))
    NotesScreenShared(state = mockNoteState, effect = MutableSharedFlow(), onAction = {}) {

    }
}

