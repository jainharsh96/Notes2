package com.notes.shared.db


import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.notes.shared.ui.uientity.NoteEntity
import com.notes.shared.utils.DateFormatter

@Immutable
@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var body: String? = null,
    @ColumnInfo(name = "created_date")
    var createdDate: Long? = null,
    @ColumnInfo(name = "updated_date")
    var updatedDate: Long? = null,
    var state: Int = SAVED,
) {
    companion object {
        var SAVED = 0
        var DRAFTED = 1
    }

    @Ignore
    private var firstLine: String = ""

    @Ignore
    private var secondLine: String = ""

    fun firstLineData(): String {
        if (firstLine.isEmpty()) {
            firstLine = body?.split("\n")?.get(0) ?: ""
        }
        return firstLine
    }

    fun secondLineData(): String {
        if (secondLine.isEmpty()) {
            val index = body?.indexOf("\n") ?: 0
            secondLine = if (index != -1) {
                body?.substring(index + 1) ?: ""
            } else {
                firstLineData()
            }.trim()
        }
        return secondLine
    }
}

fun Note.toNoteEntity() = NoteEntity(
    id = this.id, body = this.body,
    createdDate = DateFormatter.format(this.createdDate ?: 0, format = DateFormatter.NOTE_DATE_FORMAT),
    updatedDate = DateFormatter.format(this.updatedDate ?: 0, format = DateFormatter.NOTE_DATE_FORMAT), state = this.state
)

fun NoteEntity.toNote() = Note(
    id = this.id, body = this.body,
    createdDate = DateFormatter.formatInLong(this.createdDate.orEmpty(), format = DateFormatter.NOTE_DATE_FORMAT),
    updatedDate = DateFormatter.formatInLong(this.updatedDate.orEmpty(), format = DateFormatter.NOTE_DATE_FORMAT), state = this.state
)