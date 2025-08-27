package com.notes.shared.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.notes.shared.ui.NotesRoutes.ARG_IS_DRAFT_SCREEN
import com.notes.shared.ui.NotesRoutes.ARG_NOTES_ID
import com.notes.shared.ui.NotesRoutes.ARG_OPEN_RECORDING
import com.notes.shared.ui.NotesRoutes.ARG_REMINDER_ID

object NotesRoutes {
    const val ARG_NOTES_ID = "notes_id"
    const val ARG_REMINDER_ID = "reminder_id"
    const val ARG_OPEN_RECORDING = "open_recording"
    const val ARG_IS_DRAFT_SCREEN = "is_draft_screen"
    const val NOTES_SCREEN_ROUTE = "notes_screen"
    const val CREATE_NOTES_SCREEN_ROUTE = "create_notes_screen"
    const val NOTES_SETTING_SCREEN_ROUTE = "notes_setting_screen"
    const val NOTES_SECURE_LOCK_SCREEN = "notes_secure_lock"
    const val SHOW_ALL_REMINDER_SCREEN = "show_all_reminder_screen"
    const val ADD_EDIT_REMINDER_SCREEN = "add_edit_reminder_screen"
}

sealed class NotesNavigation(
    val arguments: List<NamedNavArgument>,
    val destination: String
) {
    object SecureLockScreen : NotesNavigation(arguments = emptyList(), destination = NotesRoutes.NOTES_SECURE_LOCK_SCREEN) {
        fun path() = NotesRoutes.NOTES_SECURE_LOCK_SCREEN
    }

    object NotesScreen :
        NotesNavigation(
            arguments = listOf(navArgument(ARG_IS_DRAFT_SCREEN) { defaultValue = false }),
            destination = NotesRoutes.NOTES_SCREEN_ROUTE + "?$ARG_IS_DRAFT_SCREEN={$ARG_IS_DRAFT_SCREEN}"
        ) {
        fun path(isDraftScreen: Boolean = false) =
            NotesRoutes.NOTES_SCREEN_ROUTE + "?$ARG_IS_DRAFT_SCREEN=$isDraftScreen"
    }

    object CreateNotesScreen : NotesNavigation(
        arguments = listOf(navArgument(ARG_NOTES_ID) {
            type = NavType.IntType
            defaultValue = -1
        }, navArgument(ARG_OPEN_RECORDING) {
            type = NavType.BoolType
            defaultValue = false
        }),
        destination = NotesRoutes.CREATE_NOTES_SCREEN_ROUTE + "?$ARG_NOTES_ID={$ARG_NOTES_ID}" + "&$ARG_OPEN_RECORDING={$ARG_OPEN_RECORDING}"
    ) {
        fun path(notesId: Int?, openRecording: Boolean = false) =
            NotesRoutes.CREATE_NOTES_SCREEN_ROUTE + "?$ARG_NOTES_ID=$notesId" + "&$ARG_OPEN_RECORDING=$openRecording"
    }

    object NotesSettingScreen : NotesNavigation(
        arguments = emptyList(),
        destination = NotesRoutes.NOTES_SETTING_SCREEN_ROUTE
    ) {
        fun path() = NotesRoutes.NOTES_SETTING_SCREEN_ROUTE
    }

    object ShowAllReminderScreen : NotesNavigation(
        arguments = listOf(navArgument(ARG_NOTES_ID) {
            type = NavType.IntType
            defaultValue = -1
        }),
        destination = NotesRoutes.SHOW_ALL_REMINDER_SCREEN + "?$ARG_NOTES_ID={$ARG_NOTES_ID}"
    ) {
        fun path(notesId: Int? = -1) =
            NotesRoutes.SHOW_ALL_REMINDER_SCREEN + "?$ARG_NOTES_ID=$notesId"
    }

    object AddEditReminderScreen : NotesNavigation(
        arguments = listOf(navArgument(ARG_NOTES_ID) {
            type = NavType.IntType
            defaultValue = -1
        }, navArgument(ARG_REMINDER_ID) {
            type = NavType.IntType
            defaultValue = -1
        }),
        destination = NotesRoutes.ADD_EDIT_REMINDER_SCREEN + "?$ARG_NOTES_ID={$ARG_NOTES_ID}" + "&$ARG_REMINDER_ID={$ARG_REMINDER_ID}"
    ) {
        fun path(noteId: Int? = -1, reminderId : Int? = -1) =
            NotesRoutes.ADD_EDIT_REMINDER_SCREEN + "?$ARG_NOTES_ID=$noteId" + "&$ARG_REMINDER_ID=$reminderId"
    }
}