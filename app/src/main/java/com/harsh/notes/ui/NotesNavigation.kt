package com.harsh.notes.ui

//import androidx.navigation.NamedNavArgument
//import androidx.navigation.NavType
//import androidx.navigation.navArgument
//import com.harsh.notes.ui.NotesRoutes.ARG_IS_DRAFT_SCREEN
//import com.harsh.notes.ui.NotesRoutes.ARG_NOTES_ID
//
//object NotesRoutes {
//    const val ARG_NOTES_ID = "notes_id"
//    const val ARG_IS_DRAFT_SCREEN = "is_draft_screen"
//    const val NOTES_SCREEN_ROUTE = "notes_screen"
//    const val CREATE_NOTES_SCREEN_ROUTE = "create_notes_screen"
//    const val NOTES_SETTING_SCREEN_ROUTE = "notes_setting_screen"
//}
//
//sealed class NotesNavigation(
//    val arguments: List<NamedNavArgument>,
//    val destination: String
//) {
//    object NotesScreen :
//        NotesNavigation(
//            arguments = listOf(navArgument(ARG_IS_DRAFT_SCREEN) { type = NavType.StringType }),
//            destination = NotesRoutes.NOTES_SCREEN_ROUTE + "?$ARG_IS_DRAFT_SCREEN={$ARG_IS_DRAFT_SCREEN}"
//        ) {
//        fun path(isDraftScreen: Boolean = false) =
//            NotesRoutes.NOTES_SCREEN_ROUTE + "?$ARG_IS_DRAFT_SCREEN={$isDraftScreen}"
//    }
//
//    object CreateNotesScreen : NotesNavigation(
//        arguments = listOf(navArgument(ARG_NOTES_ID) { type = NavType.StringType }),
//        destination = NotesRoutes.CREATE_NOTES_SCREEN_ROUTE + "?$ARG_NOTES_ID={$ARG_NOTES_ID}"
//    ) {
//        fun path(notesId: Int) =
//            NotesRoutes.CREATE_NOTES_SCREEN_ROUTE + "?$ARG_NOTES_ID={$notesId}"
//    }
//
//    object NotesSettingScreen : NotesNavigation(
//        arguments = emptyList(),
//        destination = NotesRoutes.NOTES_SETTING_SCREEN_ROUTE
//    )
//}