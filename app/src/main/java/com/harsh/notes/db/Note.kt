package com.harsh.notes.db


import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Immutable
@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var body: String? = null,
    @ColumnInfo(name = "created_date")
    var createdDate: Date? = null,
    @ColumnInfo(name = "updated_date")
    var updatedDate: Date? = null,
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