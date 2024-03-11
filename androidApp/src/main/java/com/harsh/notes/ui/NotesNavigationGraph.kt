package com.harsh.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.harsh.notes.ui.createnotescreen.CreateNoteScreen
import com.harsh.notes.ui.createnotescreen.CreateNoteViewModel
import com.harsh.notes.ui.notesscreen.NotesScreen
import com.harsh.notes.ui.notesscreen.NotesViewModel
import com.harsh.notes.ui.settingscreen.SettingScreen
import com.notes.shared.ui.NavigationAction


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
            val viewModel = hiltViewModel<NotesViewModel>(it)
            val state by viewModel.state.collectAsState()
            val event = remember(viewModel) {
                return@remember viewModel::event
            }
            NotesScreen(
                state = state,
                effect = viewModel.sideEffect,
                onAction = onAction,
                event = event
            )
        }
        composable(
            route = NotesNavigation.CreateNotesScreen.destination,
            arguments = NotesNavigation.CreateNotesScreen.arguments
        ) {
            val viewModel = hiltViewModel<CreateNoteViewModel>(it)
            val state by viewModel.state.collectAsState()
            val event = remember(viewModel) {
                return@remember viewModel::event
            }
            CreateNoteScreen(
                state = state,
                effect = viewModel.sideEffect,
                onAction = onAction,
                event = event
            )
        }
        composable(
            route = NotesNavigation.NotesSettingScreen.destination,
            arguments = NotesNavigation.NotesSettingScreen.arguments
        ) {
            SettingScreen(onAction = onAction)
        }
    }
}