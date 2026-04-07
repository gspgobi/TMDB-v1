package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.EpisodeDto
import com.gobidev.tmdbv1.data.remote.dto.SeasonDto
import com.gobidev.tmdbv1.data.remote.dto.TvCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvKeywordsResponse
import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.data.remote.dto.TvDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvDto
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.Season
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

fun SeasonDto.toSeason(): Season = Season(
    id = id,
    name = name,
    overview = overview ?: "",
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    seasonNumber = seasonNumber,
    episodeCount = episodeCount,
    airDate = airDate?.take(4) ?: ""  // just the year
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
    status = status,
    seasons = seasons
        ?.filter { it.seasonNumber > 0 }  // exclude "Specials" (season 0)
        ?.map { it.toSeason() }
        ?: emptyList()
)

private const val STILL_SIZE = "w300"

fun EpisodeDto.toEpisode(): Episode = Episode(
    id = id,
    name = name,
    overview = overview ?: "",
    episodeNumber = episodeNumber,
    seasonNumber = seasonNumber,
    airDate = airDate?.toFormattedDate() ?: "",
    stillUrl = stillPath?.let { "$IMAGE_BASE_URL$STILL_SIZE$it" },
    rating = voteAverage,
    runtime = runtime
)

fun TvCreditsResponse.toTvCredits(): TvCredits = TvCredits(
    id = id,
    cast = cast.map { it.toCastMember() },
    crew = crew?.map { it.toCrewMember() } ?: emptyList()
)

fun TvKeywordsResponse.toKeywords(): List<Keyword> =
    results.map { Keyword(id = it.id, name = it.name) }
