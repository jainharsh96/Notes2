package com.harsh.notes.di

import android.content.Context
import com.harsh.notes.AppDispatcherProvider
import com.harsh.notes.AppDispatcherImpl
import com.harsh.notes.db.NotesDao
import com.harsh.notes.db.NotesDatabase
import com.harsh.notes.db.NotesDb
import com.harsh.notes.repository.NotesRepository
import com.harsh.notes.repository.NotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
internal object BaseModule {
    @[Provides Singleton]
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase {
        return NotesDb.getDatabase(context)
    }

    @[Provides Singleton]
    fun provideNotesDao(notesDatabase: NotesDatabase): NotesDao {
        return notesDatabase.notesDao()
    }
}

@[Module InstallIn(SingletonComponent::class)]
internal interface NotesModule {
    @[Binds Singleton]
    fun provideNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @[Binds Singleton]
    fun provideDispatcher(dispatcherImpl: AppDispatcherImpl): AppDispatcherProvider
}