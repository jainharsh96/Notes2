package com.harsh.notes

import android.app.Application
import com.notes.shared.NotesAndroidDependenciesInitializer
import com.notes.shared.setApplicationContext
import com.notes.shared.utils.NotesLogger


class NotesApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        addUncaughtExceptionHandler()
        setApplicationContext(this)
        NotesAndroidDependenciesInitializer.init(
            context = this,
        )
    }

    fun addUncaughtExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            NotesLogger.log(
                "UncaughtException",
                "Thread: ${thread.name} \n Exception: ${throwable.stackTraceToString()}"
            )
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}