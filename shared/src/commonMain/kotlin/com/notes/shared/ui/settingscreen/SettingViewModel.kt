package com.notes.shared.ui.settingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.shared.NotesDependencies
import com.notes.shared.repository.NotesRepository
import com.notes.shared.ui.uientity.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class SettingViewModel constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    fun insertNotes(list: List<NoteEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.insertNotes(list)
        }
    }

    fun getNotesSyncManager() = NotesDependencies.notesSyncManager
}