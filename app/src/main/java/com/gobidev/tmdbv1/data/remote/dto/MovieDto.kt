package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for popular movies API response.
 * Matches the exact structure returned by TMDB API.
 */
data class MovieListPagedResponse(
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
data class MovieDetailsResponse(
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
    val originalTitle: String?,

    @SerializedName("belongs_to_collection")
    val belongsToCollection: BelongsToCollectionDto? = null
)

data class BelongsToCollectionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
)

data class CollectionDetailsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("parts") val parts: List<MovieDto>
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
data class MovieCreditsResponse(
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

/**
 * Data Transfer Object for movie reviews API response.
 */
data class MovieReviewsPagedResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<ReviewDto>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)

/**
 * Data Transfer Object for a movie review.
 */
data class ReviewDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("author_details")
    val authorDetails: AuthorDetailsDto,

    @SerializedName("content")
    val content: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("url")
    val url: String?
)

data class ExternalIdsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("wikidata_id") val wikidataId: String?,
    @SerializedName("facebook_id") val facebookId: String?,
    @SerializedName("instagram_id") val instagramId: String?,
    @SerializedName("twitter_id") val twitterId: String?,
    @SerializedName("tvdb_id") val tvdbId: String? = null
)

/**
 * Data Transfer Object for review author details.
 */
data class AuthorDetailsDto(
    @SerializedName("name")
    val name: String?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("avatar_path")
    val avatarPath: String?,

    @SerializedName("rating")
    val rating: Double?
)

data class MovieKeywordsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("keywords") val keywords: List<KeywordDto>
)

// TV keywords API uses "results" instead of "keywords"
data class TvKeywordsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<KeywordDto>
)

data class KeywordDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class MovieVideosResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<VideoDto>
)

data class VideoDto(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("site") val site: String,
    @SerializedName("type") val type: String,
    @SerializedName("official") val official: Boolean,
    @SerializedName("published_at") val publishedAt: String
)

data class MovieImagesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("backdrops") val backdrops: List<ImageDto>,
    @SerializedName("posters") val posters: List<ImageDto>,
    @SerializedName("logos") val logos: List<ImageDto>
)

data class ImageDto(
    @SerializedName("file_path") val filePath: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("aspect_ratio") val aspectRatio: Double,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("iso_639_1") val language: String?
)

