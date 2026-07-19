package com.gobidev.tmdbv1.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotifiedMovieDao {
    @Query("SELECT movieId FROM notified_movies")
    suspend fun getAllNotifiedIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markNotified(entity: NotifiedMovieEntity)
}
