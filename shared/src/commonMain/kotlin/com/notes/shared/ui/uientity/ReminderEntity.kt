package com.notes.shared.ui.uientity

import com.notes.shared.utils.DateFormatter
import com.notes.shared.utils.DateFormatter.NOTE_DATE_FORMAT
import kotlin.jvm.JvmInline

data class ReminderEntity(
    val id: Int = 0,
    val title: String,
    var createdDate: Long?,
    var updatedDate: Long?,
    val remindAt: Long,
    val state: ReminderState,
    val linkedNoteId: Int?,
) {
    private var firstLine: String = ""

    private var secondLine: String = ""

    fun firstLineData(): String {
        if (firstLine.isEmpty()) {
            firstLine = title.split("\n").getOrNull(0).orEmpty()
        }
        return firstLine
    }

    fun secondLineData(): String {
        if (secondLine.isEmpty()) {
            val index = title.indexOf("\n")
            secondLine = if (index != -1 && index + 1 < title.length) {
                title.substring(index + 1)
            } else {
                firstLineData()
            }.trim()
        }
        return secondLine
    }

    fun isReminderSet(): Boolean {
        return state == ReminderState.SET
    }

    fun getRemindAtTime(): String {
        return DateFormatter.format(remindAt, NOTE_DATE_FORMAT)
    }
}


@JvmInline
value class ReminderState private constructor(val value: Int) {
    companion object {
        val SET = ReminderState(0)
        val COMPLETED = ReminderState(1)
        val PAUSED = ReminderState(2)

        fun getState(value: Int) : ReminderState {
            return when(value) {
                0 -> SET
                1 -> COMPLETED
                2 -> PAUSED
                else -> SET
            }
        }

        fun getIncompletedStates() = listOf(SET.value, PAUSED.value)
    }
}