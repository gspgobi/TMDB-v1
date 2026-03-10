package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.SearchResultDto
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.Person
import com.gobidev.tmdbv1.domain.model.SearchResult
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.util.toFormattedDate

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"

fun SearchResultDto.toSearchResult(): SearchResult? {
    return when (mediaType) {
        "movie" -> SearchResult.MovieResult(
            Movie(
                id = id,
                title = title ?: return null,
                overview = overview ?: "",
                posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
                backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
                releaseDate = releaseDate?.toFormattedDate() ?: "Unknown",
                rating = voteAverage ?: 0.0,
                voteCount = voteCount ?: 0
            )
        )
        "tv" -> SearchResult.TvResult(
            TvShow(
                id = id,
                name = name ?: return null,
                overview = overview ?: "",
                posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
                backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
                firstAirDate = firstAirDate?.toFormattedDate() ?: "Unknown",
                rating = voteAverage ?: 0.0,
                voteCount = voteCount ?: 0
            )
        )
        "person" -> SearchResult.PersonResult(
            Person(
                id = id,
                name = name ?: return null,
                profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" },
                knownForDepartment = knownForDepartment ?: "Acting"
            )
        )
        else -> null
    }
}
