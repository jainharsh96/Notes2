package com.notes.shared.ui.reminders

import androidx.lifecycle.SavedStateHandle
import com.notes.shared.AppDispatcherProvider
import com.notes.shared.repository.ReminderRepository
import com.notes.shared.ui.BaseViewModel
import com.notes.shared.ui.NotesRoutes.ARG_NOTES_ID
import com.notes.shared.ui.NotesRoutes.ARG_REMINDER_ID
import com.notes.shared.ui.uientity.ReminderEntity
import com.notes.shared.ui.uientity.ReminderState
import com.notes.shared.utils.DateFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class AddEditReminderViewModel constructor(
    private val savedStateHandle: SavedStateHandle,
    private val reminderRepo: ReminderRepository,
    private val dispatcher: AppDispatcherProvider,
) : BaseViewModel<AddEditReminderContract.State, AddEditReminderContract.Event, AddEditReminderContract.SideEffect>() {

    private val noteId: Int? = savedStateHandle[ARG_NOTES_ID]
    private val reminderId: Int? = savedStateHandle[ARG_REMINDER_ID]
    private val _state = MutableStateFlow(AddEditReminderContract.State.initialState())
    override val state: StateFlow<AddEditReminderContract.State>
        get() = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddEditReminderContract.SideEffect>()
    override val sideEffect: SharedFlow<AddEditReminderContract.SideEffect>
        get() = _sideEffect.asSharedFlow()

    override fun event(event: AddEditReminderContract.Event): Unit = launchCoroutine {
        when (event) {
            AddEditReminderContract.Event.ClickBack -> _sideEffect.emit(AddEditReminderContract.SideEffect.ClickBack)
            AddEditReminderContract.Event.LoadReminder -> loadReminder()
            is AddEditReminderContract.Event.OnTypeTitle -> _state.update {
                val newReminder = it.reminderEntity?.copy(title = event.title) ?: return@update it
                it.copy(reminderEntity = newReminder)
            }

            AddEditReminderContract.Event.SaveReminder -> saveReminder()
            is AddEditReminderContract.Event.OnSetFrequency -> _state.update {
                val enteredNumber = event.freq.toIntOrNull() ?: 0
                val newReminder = it.reminderEntity?.copy(frequency = enteredNumber) ?: return@update it
                it.copy(reminderEntity = newReminder)
            }
            is AddEditReminderContract.Event.OnSetRemindAt -> {
                _state.update {
                    val newReminder = it.reminderEntity?.copy(remindAtDate = event.date) ?: return@update it
                    it.copy(reminderEntity = newReminder)
                }
            }
        }
    }

    private suspend fun saveReminder() = withContext(dispatcher.IO) {
        if (_state.value.allowToSave()) {
            val reminderEntity = _state.value.reminderEntity ?: return@withContext
            reminderRepo.updateOrInsertReminder(reminderEntity)
            _sideEffect.emit(AddEditReminderContract.SideEffect.ClickBack)
        }
    }

    private suspend fun loadReminder() = withContext(dispatcher.IO) {
        val reminder = reminderId?.let { reminderRepo.fetchReminder(it) }
        val newReminder = reminder ?: ReminderEntity(
            title = "",
            createdDate = DateFormatter.currentDateTimeMillisecond(),
            updatedDate = DateFormatter.currentDateTimeMillisecond(),
            remindAtDate = DateFormatter.format(DateFormatter.currentDateTimeMillisecond(), DateFormatter.NOTE_DATE_FORMAT),
            frequency = ReminderEntity.FREQUENCY_NOT_REPEAT,
            state = ReminderState.SET,
            linkedNoteId = noteId,
        )
        _state.update {
            it.copy(
                isLoading = false,
                isEditing = reminder != null,
                reminderEntity = newReminder
            )
        }
    }
}