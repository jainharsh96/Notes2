package com.notes.shared.ui.uientity

import androidx.compose.runtime.Immutable

@Immutable
data class NoteEntity(
    var id: Int = 0,
    var body: String? = null,
    var createdDate: Long? = null,
    var updatedDate: Long? = null,
    var state: Int = SAVED,
) {
    companion object {
        var SAVED = 0
        var DRAFTED = 1
    }

    private var firstLine: String = ""

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