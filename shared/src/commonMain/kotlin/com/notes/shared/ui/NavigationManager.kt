package com.notes.shared.ui

sealed class NavigationAction {
    object Back : NavigationAction()
    data class NavigateToCreateNoteScreen(val noteId: Int?, val openRecording : Boolean) : NavigationAction()
    object NavigateToSettingScreen : NavigationAction()
    object NavigateToNotesScreen : NavigationAction()
    object RestoreData : NavigationAction()
    object OpenDraftNote : NavigationAction()
    object RecordNotes : NavigationAction()
    object GotoLockScreen : NavigationAction()
    data class NavigateToAddEditReminderScreen(val reminderId: Int?, val noteId: Int?) : NavigationAction()
    data class NavigateToShowAllRemindersScreen(val noteId: Int?) : NavigationAction()
}