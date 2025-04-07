package com.harsh.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.harsh.notes.NotesSyncManagerAndroidImpl
import com.notes.shared.DatabasePasswordProviderAndroidImpl
import com.notes.shared.NotesDependencies
import com.notes.shared.ui.NotesApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NotesActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        NotesDependencies.init(
            notesSyncManager = NotesSyncManagerAndroidImpl(this),
            databasePasswordProvider = DatabasePasswordProviderAndroidImpl(this.applicationContext)
        )
        setContent {
            val systemUiController = rememberSystemUiController()
            LaunchedEffect(Unit) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = true
                )
            }
            NotesApp()
        }
    }

    override fun onDestroy() {
        NotesDependencies.clearData()
        super.onDestroy()
    }
}