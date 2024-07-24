package com.notes.shared.utils

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatter {

    fun format(dateInMillis: Long, pattern: String): String {
        val instant = Instant.fromEpochMilliseconds(dateInMillis)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime.toString(pattern)
    }

    // Extension function to format LocalDateTime using a pattern
    fun LocalDateTime.toString(pattern: String): String {
        return "${this.dayOfMonth} ${this.month} ${this.year} ${this.hour}-${this.minute}"   // TODO IMPLEMENT PATTERN
    }
}