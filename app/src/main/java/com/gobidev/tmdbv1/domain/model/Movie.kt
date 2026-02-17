package com.gobidev.tmdbv1.domain.model

/**
 * Domain model for a movie in the popular movies list.
 * Clean, presentation-ready model without API implementation details.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val rating: Double,
    val voteCount: Int
)

/**
 * Domain model for detailed movie information.
 * Used on the movie details screen.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val rating: Double,
    val voteCount: Int,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?
)

/**
 * Domain model for genre information.
 */
data class Genre(
    val id: Int,
    val name: String
)
