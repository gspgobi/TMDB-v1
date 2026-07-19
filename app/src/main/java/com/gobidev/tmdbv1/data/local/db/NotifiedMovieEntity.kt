package com.gobidev.tmdbv1.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notified_movies")
data class NotifiedMovieEntity(
    @PrimaryKey val movieId: Int,
    val notifiedAt: Long
)
