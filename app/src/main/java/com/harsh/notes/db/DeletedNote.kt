package com.harsh.notes.db

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/*
for testing app
 */
@Immutable
@Entity(tableName = "deleted_note")
data class DeletedNote(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var body: String? = null,
    @ColumnInfo(name = "created_date")
    var createdDate: Date? = null,
    @ColumnInfo(name = "updated_date")
    var date: Date? = null,
    var state: Int = SAVED,
) {
    companion object {
        var SAVED = 0
        var DRAFTED = 1

        fun cloneNote(note: Note) =
            DeletedNote(
                id = note.id,
                body = note.body,
                createdDate = note.createdDate,
                date = note.updatedDate,
                state = note.state
            )
    }
}