package com.gobidev.tmdbv1.domain.model

data class TvShow(
    val id: Int,
    val name: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val firstAirDate: String,
    val rating: Double,
    val voteCount: Int
)

data class Season(
    val id: Int,
    val name: String,
    val overview: String,
    val posterUrl: String?,
    val seasonNumber: Int,
    val episodeCount: Int,
    val airDate: String
)

data class TvShowDetails(
    val id: Int,
    val name: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val firstAirDate: String,
    val rating: Double,
    val voteCount: Int,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?,
    val seasons: List<Season>
)

data class TvCredits(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)

data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val airDate: String,
    val stillUrl: String?,
    val rating: Double,
    val runtime: Int?
)
