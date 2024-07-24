package com.harsh.notes.ui.settingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.repository.NotesRepository
import com.notes.shared.ui.uientity.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val notesRepository: NotesRepository) :
    ViewModel() {

    fun insertNotes(list: List<NoteEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.insertNotes(list)
        }
    }
}