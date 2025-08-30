package com.notes.shared.di

import com.notes.shared.AppDispatcherImpl
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.NotesDependencies
import com.notes.shared.ScreenLockUtil
import com.notes.shared.domain.NotesDbUseCase
import com.notes.shared.repository.NotesRepository
import com.notes.shared.repository.NotesRepositoryImpl
import com.notes.shared.repository.ReminderRepository
import com.notes.shared.repository.ReminderRepositoryImpl
import com.notes.shared.ui.createnotescreen.CreateNoteViewModel
import com.notes.shared.ui.notesscreen.NotesViewModel
import com.notes.shared.ui.reminders.AddEditReminderViewModel
import com.notes.shared.ui.reminders.ShowAllReminderViewModel
import com.notes.shared.ui.securelockScreen.SecureLockScreenViewmodel
import com.notes.shared.ui.settingscreen.SettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val notesRepo = module {
    singleOf(::NotesRepositoryImpl).bind(NotesRepository::class)
    singleOf(::ReminderRepositoryImpl).bind(ReminderRepository::class)
}

val dispatcherModule = module {
    singleOf(::AppDispatcherImpl).bind(AppDispatcherProvider::class)
}

val viewModels = module {
    viewModelOf(::NotesViewModel)
    viewModelOf(::CreateNoteViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::SecureLockScreenViewmodel)
    viewModelOf(::AddEditReminderViewModel)
    viewModelOf(::ShowAllReminderViewModel)
}

val utils = module {
    single { CoroutineScope(Dispatchers.IO) }  // provide global scope
}

val useCaseModule = module {
    factory { NotesDbUseCase() }
    single { ScreenLockUtil(dataStore = NotesDependencies.dataStore) }
}

val notesModule = listOf(
    notesRepo, dispatcherModule, viewModels, useCaseModule, utils
)