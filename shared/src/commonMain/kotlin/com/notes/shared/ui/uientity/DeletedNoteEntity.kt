package com.notes.shared.ui.uientity

import androidx.compose.runtime.Immutable
import com.notes.shared.utils.DateFormatter

/*
for testing app
 */
@Immutable
data class DeletedNote(
    var id: Int = 0,
    var body: String? = null,
    var createdDate: Long? = null,
    var date: Long? = null,
    var state: Int = SAVED,
) {
    companion object {
        var SAVED = 0
        var DRAFTED = 1

        fun cloneNote(note: NoteEntity) =
            DeletedNote(
                id = note.id,
                body = note.body,
                createdDate = DateFormatter.formatInLong(note.createdDate.orEmpty(), format = DateFormatter.NOTE_DATE_FORMAT),
                date = DateFormatter.formatInLong(note.updatedDate.orEmpty(), format = DateFormatter.NOTE_DATE_FORMAT),
                state = note.state
            )
    }
}