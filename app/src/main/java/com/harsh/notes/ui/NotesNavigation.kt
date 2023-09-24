package com.harsh.notes.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.harsh.notes.ui.NotesRoutes.ARG_IS_DRAFT_SCREEN
import com.harsh.notes.ui.NotesRoutes.ARG_NOTES_ID
import com.harsh.notes.ui.NotesRoutes.ARG_OPEN_RECORDING

object NotesRoutes {
    const val ARG_NOTES_ID = "notes_id"
    const val ARG_OPEN_RECORDING = "open_recording"
    const val ARG_IS_DRAFT_SCREEN = "is_draft_screen"
    const val NOTES_SCREEN_ROUTE = "notes_screen"
    const val CREATE_NOTES_SCREEN_ROUTE = "create_notes_screen"
    const val NOTES_SETTING_SCREEN_ROUTE = "notes_setting_screen"
}

sealed class NotesNavigation(
    val arguments: List<NamedNavArgument>,
    val destination: String
) {
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
        }, navArgument(ARG_OPEN_RECORDING) { defaultValue = false }),
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
}