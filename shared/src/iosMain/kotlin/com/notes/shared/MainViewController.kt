package com.notes.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.notes.shared.ui.NotesApp
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { NotesApp() }