package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrendingResponseDto(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<SearchResultDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResultPagedResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<SearchResultDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class SearchResultDto(
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: String,
    // Movie fields
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    // TV fields
    @SerializedName("name") val name: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    // Person fields
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("known_for_department") val knownForDepartment: String?
)
