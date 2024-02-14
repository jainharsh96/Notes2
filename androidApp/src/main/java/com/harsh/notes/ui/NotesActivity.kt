package com.harsh.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesActivity : BaseActivity() {

    companion object {
        private val TAG = NotesActivity::class.java.simpleName
        private const val INTENT_DRAFT_SCREEN = "intent_draft"

        fun getIntent(context: Context, isDraftScreen: Boolean = false): Intent {
            return Intent(context, NotesActivity::class.java).apply {
                putExtra(INTENT_DRAFT_SCREEN, isDraftScreen)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NotesNavigationGraph(navController = navController,
                startDestination = NotesNavigation.NotesScreen.path(isDraftScreen = isDraftScreen()),
                onAction = remember {
                    return@remember {
                        handleNavigationActions(navController, it)
                    }
                })
        }
    }

    private fun handleNavigationActions(
        navController: NavHostController,
        action: NavigationAction
    ) {
        lifecycleScope.launchWhenResumed {
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
            }
        }
    }

    private fun restoreData(){
        // TODO
    }

    private fun isDraftScreen() = intent.getBooleanExtra(INTENT_DRAFT_SCREEN, false)
}