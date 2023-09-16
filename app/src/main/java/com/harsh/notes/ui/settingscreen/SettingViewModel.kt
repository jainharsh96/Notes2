package com.harsh.notes.ui.settingscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.notes.db.Note
import com.harsh.notes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val notesRepository: NotesRepository) :
    ViewModel() {

    fun insertNotes(list: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.insertNotes(list)
        }
    }
}