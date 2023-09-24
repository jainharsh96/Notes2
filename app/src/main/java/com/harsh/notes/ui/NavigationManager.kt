package com.harsh.notes.ui

import com.harsh.notes.ui.settingscreen.SettingContract
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    private val commend = MutableStateFlow<NavigationAction>(NavigationAction.None)

    fun doAction(action: NavigationAction) {
        commend.tryEmit(action)
    }

    fun observeAction() = commend

}

sealed class NavigationAction {
    object None : NavigationAction()
    object Back : NavigationAction()
    data class NavigateToCreateNoteScreen(val noteId: Int?, val openRecording : Boolean) : NavigationAction()   // todo handle nullable noteid
    object NavigateToSettingScreen : NavigationAction()
    object NavigateToNotesScreen : NavigationAction()
    object ClickRecordNotes : NavigationAction()
    object RestoreData : NavigationAction()
    object OpenDraftNote : NavigationAction()
}