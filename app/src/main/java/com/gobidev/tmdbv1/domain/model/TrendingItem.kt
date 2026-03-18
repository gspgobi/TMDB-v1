package com.gobidev.tmdbv1.domain.model

data class TrendingItem(
    val id: Int,
    val title: String,
    val backdropUrl: String?,
    val posterUrl: String?,
    val rating: Double,
    val releaseDate: String?,
    val mediaType: String
)
