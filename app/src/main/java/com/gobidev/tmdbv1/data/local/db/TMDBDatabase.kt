package com.gobidev.tmdbv1.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NotifiedMovieEntity::class, AccountMediaEntity::class, AccountMediaRemoteKeyEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TMDBDatabase : RoomDatabase() {
    abstract fun notifiedMovieDao(): NotifiedMovieDao
    abstract fun accountMediaDao(): AccountMediaDao
}
