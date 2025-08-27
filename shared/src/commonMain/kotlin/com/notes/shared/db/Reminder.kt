package com.notes.shared.db

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.ui.uientity.ReminderState

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

    val state: Int,

    val linkedNoteId : Int?
)

fun ReminderEntity.toReminder() = Reminder(
    id = this.id,
    title = this.title,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
    remindAt = this.remindAt,
    state = this.state.value,
    linkedNoteId = this.linkedNoteId
)

fun Reminder.toReminderEntity() = ReminderEntity(
    id = this.id,
    title = this.title,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
    remindAt = this.remindAt,
    state = ReminderState.getState(this.state),
    linkedNoteId = this.linkedNoteId
)