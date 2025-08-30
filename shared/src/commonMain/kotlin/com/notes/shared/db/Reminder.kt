package com.notes.shared.db

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.ui.uientity.ReminderState
import com.notes.shared.utils.DateFormatter
import com.notes.shared.utils.DateFormatter.NOTE_DATE_FORMAT

@Immutable
@Entity(tableName = "reminder")
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "created_date")
    var createdDate: Long?,
    @ColumnInfo(name = "updated_date")
    var updatedDate: Long?,
    val remindAt: Long,
    val frequency : Int,
    val state: Int,
    val linkedNoteId : Int?
)

fun ReminderEntity.toReminder() = Reminder(
    id = this.id,
    title = this.title,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
    remindAt = DateFormatter.formatInLong(remindAtDate, NOTE_DATE_FORMAT),
    frequency = this.frequency,
    state = this.state.value,
    linkedNoteId = this.linkedNoteId
)

fun Reminder.toReminderEntity() = ReminderEntity(
    id = this.id,
    title = this.title,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
    remindAtDate = DateFormatter.format(this.remindAt, NOTE_DATE_FORMAT),
    frequency = this.frequency,
    state = ReminderState.getState(this.state),
    linkedNoteId = this.linkedNoteId
)