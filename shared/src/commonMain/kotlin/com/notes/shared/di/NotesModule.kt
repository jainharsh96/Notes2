package com.notes.shared.di

import com.notes.shared.AppDispatcherImpl
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.db.NotesDatabase
import com.notes.shared.repository.NotesRepository
import com.notes.shared.repository.NotesRepositoryImpl
import com.notes.shared.ui.notesscreen.NotesViewModel
import com.notes.shared.ui.createnotescreen.CreateNoteViewModel
import com.notes.shared.ui.settingscreen.SettingViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notesDao = module {
    single { NotesDatabase.getDatabase().notesDao() }
}

val notesRepo = module {
    singleOf(::NotesRepositoryImpl).bind(NotesRepository::class)
}

val dispatcheModule = module {
    singleOf(::AppDispatcherImpl).bind(AppDispatcherProvider::class)
}

val viewModels = module {
    viewModelOf(::NotesViewModel)
    viewModelOf(::CreateNoteViewModel)
    viewModelOf(::SettingViewModel)
}

val notesModule = listOf(
    notesRepo, notesDao, dispatcheModule, viewModels
)