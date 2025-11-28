package com.notes.shared.utils

import com.notes.shared.db.Note
import com.notes.shared.db.NotesDatabaseDelegate
import com.notes.shared.getPlatform
import com.notes.shared.utils.DateFormatter.LOG_FORMAT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

object NotesLogger {

    val inMemoryLogFlow = MutableSharedFlow<InMemoryLog>(replay = 100)

    val globalScope: CoroutineScope = getKoin().get()

    fun log(tag: String, message: String) {
        globalScope.launch {
            makeDbEntry(tag, message)
        }
    }

    fun inMemoryLog(tag: String, message: String) {
        if (getPlatform().allowShowingDebugWindow()){
            globalScope.launch {
                inMemoryLogFlow.emit(
                    InMemoryLog(
                        time = DateFormatter.currentDateTime(LOG_FORMAT),
                        tag = tag,
                        message = message
                    )
                )
            }
        }
    }

    private suspend fun makeDbEntry(tag: String, msg: String) {
        runCatching {
            val isInit = NotesDatabaseDelegate.tryInitDb()
            if (isInit) {
                NotesDatabaseDelegate.databaseObj?.notesDao()?.let { dao ->
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

data class InMemoryLog(val time: String = "", val tag: String = "", val message: String = "")