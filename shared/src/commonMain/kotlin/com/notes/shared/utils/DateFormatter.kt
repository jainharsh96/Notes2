package com.notes.shared.utils

import kotlinx.datetime.*
import kotlinx.datetime.format
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

object DateFormatter {

    val timeZone = TimeZone.currentSystemDefault()
    val months = listOf(
        "JANUARY",
        "FEBRUARY",
        "MARCH",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUGUST",
        "SEPTEMBER",
        "OCTOBER",
        "NOVEMBER",
        "DECEMBER"
    ).map {
        it.substring(0, 3).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    //  "dd MMM yyyy hh:mm a"
    val NOTE_DATE_FORMAT
        get() = LocalDateTime.Format {
            dayOfMonth()
            char(' ')
            monthName(MonthNames(months))
            char(' ')
            year()
            char(' ')
            amPmHour()
            char(':')
            minute()
            char(' ')
            amPmMarker(am = "am", pm = "pm")
        }

    //  "dd MMM yyyy hh:mm:ss.123 a"
    val LOG_FORMAT
        get() = LocalDateTime.Format {
            dayOfMonth()
            char(' ')
            monthName(MonthNames(months))
            char(' ')
            year()
            char(' ')
            amPmHour()
            char(':')
            minute()
            char(':')
            second()
            char('.')
            secondFraction(3)
            char(' ')
            amPmMarker(am = "am", pm = "pm")
        }

    fun format(dateTimeInMillis: Long, format: DateTimeFormat<LocalDateTime>): String {
        val instant = Instant.fromEpochMilliseconds(dateTimeInMillis)
        val dateTime = instant.toLocalDateTime(timeZone)
        return dateTime.format(format)
    }

    fun formatInLong(date: String, format: DateTimeFormat<LocalDateTime>): Long {
        return LocalDateTime.parse(date, format).toInstant(timeZone).toEpochMilliseconds()
    }

    fun currentDateTimeMillisecond() = Clock.System.now().toEpochMilliseconds()

    fun currentDateTime(format: DateTimeFormat<LocalDateTime> = NOTE_DATE_FORMAT): String {
        return format(dateTimeInMillis = Clock.System.now().toEpochMilliseconds(), format = format)
    }
}