package com.harsh.notes

import android.app.Application
import com.notes.shared.NotesAndroidDependenciesInitializer
import com.notes.shared.di.NotesKoinAndroid
import com.notes.shared.setApplicationContext


class NotesApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        setApplicationContext(this)
        NotesAndroidDependenciesInitializer.init(
            context = this,
        )
        NotesKoinAndroid.init()
    }
}