package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for popular movies API response.
 * Matches the exact structure returned by TMDB API.
 */
data class PopularMoviesResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<MovieDto>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)

/**
 * Data Transfer Object for a movie in the list.
 * Used for both popular movies list and movie details.
 */
data class MovieDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("adult")
    val adult: Boolean,

    @SerializedName("genre_ids")
    val genreIds: List<Int>?,

    @SerializedName("original_language")
    val originalLanguage: String?,

    @SerializedName("original_title")
    val originalTitle: String?,

    @SerializedName("video")
    val video: Boolean
)

/**
 * Data Transfer Object for movie details API response.
 * Contains additional fields not present in the list response.
 */
data class MovieDetailsDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("runtime")
    val runtime: Int?,

    @SerializedName("budget")
    val budget: Long,

    @SerializedName("revenue")
    val revenue: Long,

    @SerializedName("genres")
    val genres: List<GenreDto>,

    @SerializedName("status")
    val status: String?,

    @SerializedName("tagline")
    val tagline: String?,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("adult")
    val adult: Boolean,

    @SerializedName("original_language")
    val originalLanguage: String?,

    @SerializedName("original_title")
    val originalTitle: String?
)

/**
 * Data Transfer Object for genre information.
 */
data class GenreDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)

/**
 * Data Transfer Object for movie credits API response.
 * Contains cast and crew information.
 */
data class MovieCreditsDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<CastMemberDto>,

    @SerializedName("crew")
    val crew: List<CrewMemberDto>?
)

/**
 * Data Transfer Object for cast member information.
 */
data class CastMemberDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("order")
    val order: Int,

    @SerializedName("cast_id")
    val castId: Int?,

    @SerializedName("gender")
    val gender: Int?
)

/**
 * Data Transfer Object for crew member information.
 * Not used in current implementation but included for completeness.
 */
data class CrewMemberDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("job")
    val job: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("profile_path")
    val profilePath: String?
)
