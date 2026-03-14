package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.TvCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvDto
import com.gobidev.tmdbv1.domain.model.TvCredits
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.util.toFormattedDate

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"

fun TvDto.toTvShow(): TvShow = TvShow(
    id = id,
    name = name,
    overview = overview ?: "",
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
    firstAirDate = firstAirDate?.toFormattedDate() ?: "Unknown",
    rating = voteAverage,
    voteCount = voteCount
)

fun TvDetailsResponse.toTvDetails(): TvShowDetails = TvShowDetails(
    id = id,
    name = name,
    overview = overview ?: "",
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
    firstAirDate = firstAirDate?.toFormattedDate() ?: "Unknown",
    rating = voteAverage,
    voteCount = voteCount,
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
    genres = genres.map { it.toGenre() },
    tagline = tagline,
    status = status
)

fun TvCreditsResponse.toTvCredits(): TvCredits = TvCredits(
    id = id,
    cast = cast.map { it.toCastMember() },
    crew = crew?.map { it.toCrewMember() } ?: emptyList()
)
