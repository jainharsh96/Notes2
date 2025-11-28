package com.notes.shared.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.notes.shared.getPlatform
import com.notes.shared.ui.createnotescreen.CreateNoteScreenShared
import com.notes.shared.ui.createnotescreen.CreateNoteViewModel
import com.notes.shared.ui.notesscreen.NotesScreenShared
import com.notes.shared.ui.notesscreen.NotesViewModel
import com.notes.shared.ui.reminders.AddEditReminderScreenShared
import com.notes.shared.ui.reminders.AddEditReminderViewModel
import com.notes.shared.ui.reminders.ShowAllReminderScreenShared
import com.notes.shared.ui.reminders.ShowAllReminderViewModel
import com.notes.shared.ui.securelockScreen.SecureLockScreen
import com.notes.shared.ui.securelockScreen.SecureLockScreenViewmodel
import com.notes.shared.ui.settingscreen.SettingScreenShared
import com.notes.shared.ui.settingscreen.SettingViewModel
import com.notes.shared.utils.DebugWindow
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf


@OptIn(KoinExperimentalAPI::class, ExperimentalFoundationApi::class)
@Composable
fun NotesApp(
    startDestination: String = NotesNavigation.NotesScreen.path(),
) {
    val navController = rememberNavController()
    val navActionHandler = remember { NotesActionHandler(navController) }
    KoinContext {
        Box(modifier = Modifier.fillMaxSize()) {
            var showDebugDialog by remember { mutableStateOf(false) }
            NavHost(
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = if (getPlatform().allowShowingDebugWindow()) {
                        { showDebugDialog = true }
                    } else null
                ),
                navController = navController, startDestination = startDestination
            ) {

                composable(route = NotesNavigation.SecureLockScreen.destination) {
                    val viewModel = koinViewModel<SecureLockScreenViewmodel>()
                    val state by viewModel.state.collectAsState()
                    val event = remember(viewModel) {
                        return@remember viewModel::event
                    }
                    SecureLockScreen(
                        state = state,
                        event = event,
                        effect = viewModel.sideEffect,
                        onGoBack = {
                            navActionHandler.closeApp()
                        },
                        onGoForward = {
                            navActionHandler.goToNotesApp()
                        }
                    )
                }
                composable(
                    route = NotesNavigation.NotesScreen.destination,
                    arguments = NotesNavigation.NotesScreen.arguments
                ) {
                    val savedStateHandle = it.getSavedStateHandleWithArguments()
                    val viewModel =
                        koinViewModel<NotesViewModel>() { parametersOf(savedStateHandle) }
                    val state by viewModel.state.collectAsState()
                    val event = remember(viewModel) {
                        return@remember viewModel::event
                    }
                    NotesScreenShared(
                        state = state,
                        effect = viewModel.sideEffect,
                        onAction = navActionHandler::handleNavigationActions,
                        event = event
                    )
                }
                composable(
                    route = NotesNavigation.CreateNotesScreen.destination,
                    arguments = NotesNavigation.CreateNotesScreen.arguments
                ) {
                    val savedStateHandle = it.getSavedStateHandleWithArguments()
                    val viewModel =
                        koinViewModel<CreateNoteViewModel> { parametersOf(savedStateHandle) }
                    val state by viewModel.state.collectAsState()
                    val event = remember(viewModel) {
                        return@remember viewModel::event
                    }
                    CreateNoteScreenShared(
                        state = state,
                        effect = viewModel.sideEffect,
                        onAction = navActionHandler::handleNavigationActions,
                        event = event
                    )
                }
                composable(
                    route = NotesNavigation.NotesSettingScreen.destination,
                    arguments = NotesNavigation.NotesSettingScreen.arguments
                ) {
                    val savedStateHandle = it.getSavedStateHandleWithArguments()
                    val viewModel =
                        koinViewModel<SettingViewModel> { parametersOf(savedStateHandle) }
                    SettingScreenShared(
                        onAction = navActionHandler::handleNavigationActions,
                        viewModel.getNotesSyncManager()
                    )
                }
                composable(
                    route = NotesNavigation.ShowAllReminderScreen.destination,
                    arguments = NotesNavigation.ShowAllReminderScreen.arguments
                ) {
                    val savedStateHandle = it.getSavedStateHandleWithArguments()
                    val viewModel =
                        koinViewModel<ShowAllReminderViewModel> { parametersOf(savedStateHandle) }
                    val state by viewModel.state.collectAsState()
                    val event = remember(viewModel) {
                        return@remember viewModel::event
                    }
                    ShowAllReminderScreenShared(
                        state = state,
                        effect = viewModel.sideEffect,
                        onAction = navActionHandler::handleNavigationActions,
                        event = event
                    )
                }
                composable(
                    route = NotesNavigation.AddEditReminderScreen.destination,
                    arguments = NotesNavigation.AddEditReminderScreen.arguments
                ) {
                    val savedStateHandle = it.getSavedStateHandleWithArguments()
                    val viewModel =
                        koinViewModel<AddEditReminderViewModel> { parametersOf(savedStateHandle) }
                    val state by viewModel.state.collectAsState()
                    val event = remember(viewModel) {
                        return@remember viewModel::event
                    }
                    AddEditReminderScreenShared(
                        state = state,
                        effect = viewModel.sideEffect,
                        onAction = navActionHandler::handleNavigationActions,
                        event = event
                    )
                }
            }

            if (showDebugDialog){
                DebugWindow(onDismissRequest = { showDebugDialog = false })
            }
        }
    }
}

fun restoreData() {

}

class NotesActionHandler(val navController: NavHostController) {

    fun goToNotesApp() = navController.navigate(NotesNavigation.NotesScreen.path()) {
        popUpTo(NotesNavigation.SecureLockScreen.path()) {
            inclusive = true
        }
    }

    fun closeApp() {
        navController.popBackStack(route = NotesNavigation.NotesScreen.path(), inclusive = true)
    }

    fun navigateToLockScreen() {
        navController.navigate(NotesNavigation.SecureLockScreen.path()) {
            popUpTo(route = NotesNavigation.NotesScreen.path()) {
                inclusive = true
            }
        }
    }

    fun handleNavigationActions(
        action: NavigationAction
    ) {
        when (action) {
            is NavigationAction.NavigateToCreateNoteScreen -> navController.navigate(
                NotesNavigation.CreateNotesScreen.path(
                    notesId = action.noteId,
                    openRecording = action.openRecording
                )
            )

            NavigationAction.NavigateToNotesScreen -> navController.navigate(NotesNavigation.NotesScreen.path())
            NavigationAction.NavigateToSettingScreen -> navController.navigate(
                NotesNavigation.NotesSettingScreen.path()
            )

            NavigationAction.Back -> navController.popBackStack()
            NavigationAction.OpenDraftNote -> navController.navigate(
                NotesNavigation.NotesScreen.path(
                    isDraftScreen = true
                )
            )

            NavigationAction.RestoreData -> restoreData()
            NavigationAction.RecordNotes -> {
                // TODO IMPLEMENT THIS
            }

            NavigationAction.GotoLockScreen -> navigateToLockScreen()
            is NavigationAction.NavigateToAddEditReminderScreen -> navController.navigate(
                NotesNavigation.AddEditReminderScreen.path(
                    noteId = action.noteId,
                    reminderId = action.reminderId
                )
            )

            is NavigationAction.NavigateToShowAllRemindersScreen -> navController.navigate(
                NotesNavigation.ShowAllReminderScreen.path(
                    notesId = action.noteId
                )
            )
        }
    }
}

fun NavBackStackEntry.getSavedStateHandleWithArguments(): SavedStateHandle {
    for (key in arguments?.keySet() ?: return savedStateHandle) {
        val value = arguments?.get(key)
        // Store the value in the SavedStateHandle
        savedStateHandle[key.orEmpty()] = value
    }
    return savedStateHandle
}
