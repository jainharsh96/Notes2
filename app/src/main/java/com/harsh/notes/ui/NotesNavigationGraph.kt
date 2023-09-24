package com.harsh.notes.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.harsh.notes.ui.createnotescreen.CreateNoteScreen
import com.harsh.notes.ui.notesscreen.NotesScreen
import com.harsh.notes.ui.settingscreen.SettingScreen


@Composable
fun NotesNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    onAction: (NavigationAction) -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = NotesNavigation.NotesScreen.destination,
            arguments = NotesNavigation.NotesScreen.arguments
        ) {
            NotesScreen(onAction = onAction)
        }
        composable(
            route = NotesNavigation.CreateNotesScreen.destination,
            arguments = NotesNavigation.CreateNotesScreen.arguments
        ) {
            CreateNoteScreen(onAction = onAction)
        }
        composable(
            route = NotesNavigation.NotesSettingScreen.destination,
            arguments = NotesNavigation.NotesSettingScreen.arguments
        ) {
            SettingScreen(onAction = onAction)
        }
    }
}