package com.gobidev.tmdbv1.domain.model

data class MovieBelongsToCollection(
    val id: Int,
    val name: String,
    val posterUrl: String?,
    val backdropUrl: String?
)

data class MovieCollectionDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val parts: List<Movie>
)
