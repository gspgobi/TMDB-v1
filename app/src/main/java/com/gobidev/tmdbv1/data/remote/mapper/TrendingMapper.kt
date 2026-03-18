package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.SearchResultDto
import com.gobidev.tmdbv1.domain.model.TrendingItem
import com.gobidev.tmdbv1.domain.util.toFormattedDate

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"

fun SearchResultDto.toTrendingItem(): TrendingItem? = when (mediaType) {
    "movie" -> TrendingItem(
        id = id,
        title = title ?: return null,
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        rating = voteAverage ?: 0.0,
        releaseDate = releaseDate?.toFormattedDate(),
        mediaType = "movie"
    )
    "tv" -> TrendingItem(
        id = id,
        title = name ?: return null,
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        rating = voteAverage ?: 0.0,
        releaseDate = firstAirDate?.toFormattedDate(),
        mediaType = "tv"
    )
    else -> null
}
