package com.mohsinraza.mohsinapexmusic.music.di

import android.content.Context
import com.mohsinraza.mohsinapexmusic.music.db.DatabaseDao
import com.mohsinraza.mohsinapexmusic.music.ui.screens.wrapped.WrappedAudioService
import com.mohsinraza.mohsinapexmusic.music.ui.screens.wrapped.WrappedManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WrappedModule {
    @Provides
    @Singleton
    fun provideWrappedManager(
        databaseDao: DatabaseDao,
        @ApplicationContext context: Context,
    ): WrappedManager = WrappedManager(databaseDao, context)

    @Provides
    @Singleton
    fun provideWrappedAudioService(@ApplicationContext context: Context): WrappedAudioService = WrappedAudioService(context)
}

