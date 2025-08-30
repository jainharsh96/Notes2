package com.notes.shared.utils

import com.notes.shared.db.Note
import com.notes.shared.db.NotesDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

object NotesLogger {

    val globalScope = CoroutineScope(Dispatchers.IO)

    fun log(tag : String, message: String) {
        globalScope.launch {
            makeDbEntry(tag,message)
        }
    }

    private suspend fun makeDbEntry(tag : String, msg : String) {
        runCatching {
            val isInit = NotesDatabase.tryInitDb()
            if (isInit){
                NotesDatabase.databaseObj?.notesDao()?.let { dao ->
                    val note = Note(
                        body = "$tag\n$msg",
                        createdDate = DateFormatter.currentDateTimeMillisecond(),
                        updatedDate = DateFormatter.currentDateTimeMillisecond(),
                        state = Note.DRAFTED
                    )
                    dao.insertNote(note)
                }
            }
        }
    }
}