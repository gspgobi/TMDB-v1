package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TvListPagedResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TvDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TvDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("original_name") val originalName: String?
)

data class SeasonDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("air_date") val airDate: String?
)

data class TvDetailsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    @SerializedName("genres") val genres: List<GenreDto>,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("seasons") val seasons: List<SeasonDto>?
)

data class TvCreditsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("cast") val cast: List<CastMemberDto>,
    @SerializedName("crew") val crew: List<CrewMemberDto>?
)

data class EpisodeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("runtime") val runtime: Int?
)

data class SeasonDetailsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("episodes") val episodes: List<EpisodeDto>
)
