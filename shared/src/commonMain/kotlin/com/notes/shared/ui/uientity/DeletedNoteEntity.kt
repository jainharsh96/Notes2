package com.notes.shared.ui.uientity

import androidx.compose.runtime.Immutable

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
                createdDate = note.createdDate,
                date = note.updatedDate,
                state = note.state
            )
    }
}