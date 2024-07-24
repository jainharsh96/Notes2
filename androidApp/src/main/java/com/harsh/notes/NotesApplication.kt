package com.harsh.notes

import android.app.Application
import com.notes.shared.setApplicationContext
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotesApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        setApplicationContext(this)
    }
}