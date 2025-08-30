package com.notes.shared.ui.reminders

import com.notes.shared.ui.uientity.ReminderEntity

interface AddEditReminderContract {

    data class State(
        val isLoading: Boolean,
        val isEditing : Boolean = false,
        val reminderEntity: ReminderEntity? = null
    ) {
        companion object {
            fun initialState() =
                State(isLoading = true)
        }

        fun getTitle() = if (isEditing) "Edit Reminder" else "Add Reminder"

        fun allowToSave() = (reminderEntity?.title?.isNotEmpty() == true) && reminderEntity.isRemindAtValid()
    }

    sealed class Event {
        object ClickBack : Event()
        object LoadReminder : Event()
        object SaveReminder : Event()

        data class OnTypeTitle(val title: String) : Event()
        data class OnSetRemindAt(val date: String) : Event()
        data class OnSetFrequency(val freq: String) : Event()
    }

    sealed class SideEffect {
        object ClickBack : SideEffect()
    }
}