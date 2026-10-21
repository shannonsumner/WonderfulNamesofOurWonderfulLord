package com.shannonsumner.namesofjesus.di

import android.content.Context
import androidx.room.Room
import com.shannonsumner.namesofjesus.data.BibleDao
import com.shannonsumner.namesofjesus.data.DailyDevotionalDao
import com.shannonsumner.namesofjesus.data.DevotionalDatabase
import com.shannonsumner.namesofjesus.data.DevotionalRepository
import com.shannonsumner.namesofjesus.data.KjvDatabase
import com.shannonsumner.namesofjesus.data.RoomDevotionalRepository
import com.shannonsumner.namesofjesus.data.RoomScriptureRepository
import com.shannonsumner.namesofjesus.data.ScriptureRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/** Tells Hilt how to create the databases and which repository classes to use. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDevotionalDatabase(@ApplicationContext context: Context): DevotionalDatabase =
        Room.databaseBuilder(context, DevotionalDatabase::class.java, DevotionalDatabase.FILE_NAME)
            .addCallback(DevotionalDatabase.seedCallback(context))
            .build()

    @Provides
    @Singleton
    fun provideKjvDatabase(@ApplicationContext context: Context): KjvDatabase =
        Room.databaseBuilder(context, KjvDatabase::class.java, KjvDatabase.FILE_NAME)
            .addCallback(KjvDatabase.seedCallback(context))
            .build()

    @Provides
    fun provideDailyDevotionalDao(database: DevotionalDatabase): DailyDevotionalDao =
        database.dailyDevotionalDao()

    @Provides
    fun provideBibleDao(database: KjvDatabase): BibleDao = database.bibleDao()

    /** The device clock in its current time zone; tests can supply a fixed clock instead. */
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindDevotionalRepository(repository: RoomDevotionalRepository): DevotionalRepository

    @Binds
    abstract fun bindScriptureRepository(repository: RoomScriptureRepository): ScriptureRepository
}
