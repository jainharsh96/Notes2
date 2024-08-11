package com.notes.shared.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

internal object NotesKoin {
    fun init(extraModule : List<Module> = emptyList()){
        startKoin {
            modules(notesModule + extraModule)
        }
    }
}