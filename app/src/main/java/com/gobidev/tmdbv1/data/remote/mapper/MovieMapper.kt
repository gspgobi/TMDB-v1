package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.CastMemberDto
import com.gobidev.tmdbv1.data.remote.dto.CrewMemberDto
import com.gobidev.tmdbv1.data.remote.dto.GenreDto
import com.gobidev.tmdbv1.data.remote.dto.MovieCreditsDto
import com.gobidev.tmdbv1.data.remote.dto.MovieDetailsDto
import com.gobidev.tmdbv1.data.remote.dto.MovieDto
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.model.Genre
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieCredits
import com.gobidev.tmdbv1.domain.model.MovieDetails

/**
 * Base URL for TMDB images.
 * Using w500 for posters and w780 for backdrops as recommended by TMDB.
 * Using w185 for profile images (cast members).
 */
private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"

/**
 * Extension function to map MovieDto to domain Movie model.
 * Handles null values and constructs full image URLs.
 */
fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview ?: "",
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        releaseDate = releaseDate ?: "Unknown",
        rating = voteAverage,
        voteCount = voteCount
    )
}

/**
 * Extension function to map MovieDetailsDto to domain MovieDetails model.
 * Includes genre mapping and additional fields specific to details view.
 */
fun MovieDetailsDto.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview ?: "",
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        releaseDate = releaseDate ?: "Unknown",
        rating = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genres.map { it.toGenre() },
        tagline = tagline,
        status = status
    )
}

/**
 * Extension function to map GenreDto to domain Genre model.
 */
fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

/**
 * Extension function to map MovieCreditsDto to domain MovieCredits model.
 * Converts cast and crew members and constructs full profile image URLs.
 */
fun MovieCreditsDto.toMovieCredits(): MovieCredits {
    return MovieCredits(
        id = id,
        cast = cast.map { it.toCastMember() },
        crew = crew?.map { it.toCrewMember() } ?: emptyList()
    )
}

/**
 * Extension function to map CastMemberDto to domain CastMember model.
 * Constructs full profile image URL.
 */
fun CastMemberDto.toCastMember(): CastMember {
    return CastMember(
        id = id,
        name = name,
        character = character,
        profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" },
        order = order
    )
}

/**
 * Extension function to map CrewMemberDto to domain CrewMember model.
 * Constructs full profile image URL.
 */
fun CrewMemberDto.toCrewMember(): CrewMember {
    return CrewMember(
        id = id,
        name = name,
        job = job,
        department = department,
        profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" }
    )
}

