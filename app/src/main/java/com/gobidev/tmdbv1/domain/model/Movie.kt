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

/**
 * Domain model for cast member information.
 * Represents an actor/actress in a movie.
 */
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profileUrl: String?,
    val order: Int
)

/**
 * Domain model for movie credits (cast and crew).
 * Contains list of cast members for a movie.
 */
data class MovieCredits(
    val id: Int,
    val cast: List<CastMember>
)
