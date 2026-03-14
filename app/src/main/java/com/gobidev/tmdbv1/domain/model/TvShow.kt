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
    val status: String?
)

data class TvCredits(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)
