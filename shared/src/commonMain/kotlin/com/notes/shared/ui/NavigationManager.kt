package com.notes.shared.ui

//@Singleton
//class NavigationManager @Inject constructor() {
//    private val commend = MutableStateFlow<NavigationAction>(NavigationAction.None)
//
//    fun doAction(action: NavigationAction) {
//        commend.tryEmit(action)
//    }
//
//    fun observeAction() = commend
//
//}

sealed class NavigationAction {
    object Back : NavigationAction()
    data class NavigateToCreateNoteScreen(val noteId: Int?, val openRecording : Boolean) : NavigationAction()
    object NavigateToSettingScreen : NavigationAction()
    object NavigateToNotesScreen : NavigationAction()
    object RestoreData : NavigationAction()
    object OpenDraftNote : NavigationAction()
}