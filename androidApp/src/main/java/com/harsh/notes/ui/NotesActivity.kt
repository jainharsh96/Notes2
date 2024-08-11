package com.harsh.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
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
        setContent {
            NotesApp()
        }
    }
}