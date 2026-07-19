package com.gobidev.tmdbv1.di

import android.content.Context
import androidx.room.Room
import com.gobidev.tmdbv1.data.local.db.NotifiedMovieDao
import com.gobidev.tmdbv1.data.local.db.TMDBDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing the local Room database and its DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTMDBDatabase(@ApplicationContext context: Context): TMDBDatabase {
        return Room.databaseBuilder(context, TMDBDatabase::class.java, "tmdb.db").build()
    }

    @Provides
    @Singleton
    fun provideNotifiedMovieDao(database: TMDBDatabase): NotifiedMovieDao {
        return database.notifiedMovieDao()
    }
}
