package com.notes.shared.ui.settingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.shared.NotesDependencies
import com.notes.shared.repository.NotesRepository
import com.notes.shared.ui.BaseViewModel
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingViewModel constructor(
    private val notesRepository: NotesRepository
) : BaseViewModel<Any, Any, Any>() {

    fun insertNotes(list: List<NoteEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.insertNotes(list)
        }
    }

    fun getNotesSyncManager() = NotesDependencies.notesSyncManager


    override val state: StateFlow<Any>
        get() = MutableStateFlow(Any())

    override val sideEffect: SharedFlow<Any>
        get() = MutableSharedFlow()

    override fun event(event: Any) {
    }
}