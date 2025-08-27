package com.notes.shared.ui.reminders

import com.notes.shared.ui.uientity.ReminderEntity

interface ShowAllReminderContract {

    data class State(
        val isLoading: Boolean,
        val reminders: List<ReminderEntity> = emptyList()
    ) {
        companion object {
            fun initialState(): State {
                return State(
                    isLoading = true,
                )
            }
        }
    }

    sealed class Event {
        object ClickBack : Event()
        object FetchAllReminders : Event()
        data class OnClickReminder(val reminderEntity: ReminderEntity) : Event()
        data class OnToggleSwitch(val reminderEntity: ReminderEntity, val enable: Boolean) : Event()
        object ClickAddNewReminder : Event()
    }

    sealed class SideEffect {
        object ClickBack : SideEffect()
        data class GotoAddEditReminder(val noteId : Int?, val reminderId : Int?) : SideEffect()
    }
}