package com.notes.shared.ui.reminders

import androidx.lifecycle.SavedStateHandle
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.repository.ReminderRepository
import com.notes.shared.ui.BaseViewModel
import com.notes.shared.ui.NotesRoutes.ARG_NOTES_ID
import com.notes.shared.ui.reminders.ShowAllReminderContract.SideEffect.GotoAddEditReminder
import com.notes.shared.ui.uientity.ReminderState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class ShowAllReminderViewModel constructor(
    private val savedStateHandle: SavedStateHandle,
    private val reminderRepo: ReminderRepository,
    private val dispatcher: AppDispatcherProvider,
) : BaseViewModel<ShowAllReminderContract.State, ShowAllReminderContract.Event, ShowAllReminderContract.SideEffect>() {

    private val noteId: Int? = savedStateHandle[ARG_NOTES_ID]

    private val _state = MutableStateFlow(ShowAllReminderContract.State.initialState())
    override val state: StateFlow<ShowAllReminderContract.State>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ShowAllReminderContract.SideEffect>()
    override val sideEffect: SharedFlow<ShowAllReminderContract.SideEffect>
        get() = _sideEffect.asSharedFlow()

    override fun event(event: ShowAllReminderContract.Event): Unit = launchCoroutine {
        when (event) {
            ShowAllReminderContract.Event.ClickBack -> _sideEffect.emit(ShowAllReminderContract.SideEffect.ClickBack)
            ShowAllReminderContract.Event.FetchAllReminders -> {
                fetchAllReminders(noteId = noteId)
            }

            is ShowAllReminderContract.Event.OnClickReminder -> {
                _sideEffect.emit(
                    GotoAddEditReminder(
                        noteId = null,
                        reminderId = event.reminderEntity.id
                    )
                )
            }

            is ShowAllReminderContract.Event.OnToggleSwitch -> {
                withContext(dispatcher.IO){
                    val state = if (event.enable) ReminderState.SET else ReminderState.PAUSED
                    reminderRepo.updateOrInsertReminder(event.reminderEntity.copy(state = state))
                }
            }

            ShowAllReminderContract.Event.ClickAddNewReminder -> {
                _sideEffect.emit(GotoAddEditReminder(noteId = noteId, reminderId = null))
            }
        }
    }

    private suspend fun fetchAllReminders(noteId: Int?) = withContext(dispatcher.IO) {
        if (noteId != null && noteId >= 0) {
            reminderRepo.fetchNoteReminders(noteId)
        } else {
            reminderRepo.fetchAllReminders()
        }.collect { reminders ->
            _state.update {
                it.copy(reminders = reminders, isLoading = false)
            }
        }
    }
}