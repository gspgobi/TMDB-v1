package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.BelongsToCollectionDto
import com.gobidev.tmdbv1.data.remote.dto.CastMemberDto
import com.gobidev.tmdbv1.data.remote.dto.CollectionDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.CrewMemberDto
import com.gobidev.tmdbv1.data.remote.dto.ExternalIdsResponse
import com.gobidev.tmdbv1.data.remote.dto.GenreDto
import com.gobidev.tmdbv1.data.remote.dto.ImageDto
import com.gobidev.tmdbv1.data.remote.dto.KeywordDto
import com.gobidev.tmdbv1.data.remote.dto.MovieKeywordsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieVideosResponse
import com.gobidev.tmdbv1.data.remote.dto.VideoDto
import com.gobidev.tmdbv1.data.remote.dto.MovieDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieDto
import com.gobidev.tmdbv1.data.remote.dto.MovieImagesResponse
import com.gobidev.tmdbv1.data.remote.dto.ReviewDto
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.model.Genre
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieBelongsToCollection
import com.gobidev.tmdbv1.domain.model.MovieCollectionDetails
import com.gobidev.tmdbv1.domain.util.toFormattedDate
import com.gobidev.tmdbv1.domain.model.MovieCredits
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.MovieImage
import com.gobidev.tmdbv1.domain.model.MovieImages
import com.gobidev.tmdbv1.domain.model.MovieVideo
import com.gobidev.tmdbv1.domain.model.Review

/**
 * Base URL for TMDB images.
 * Using w500 for posters and w780 for backdrops as recommended by TMDB.
 * Using w185 for profile images (cast members).
 */
private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"

/**
 * Extension function to map MovieDto to domain Movie model.
 * Handles null values and constructs full image URLs.
 */
fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview ?: "",
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        releaseDate = releaseDate?.toFormattedDate() ?: "Unknown",
        rating = voteAverage,
        voteCount = voteCount
    )
}

/**
 * Extension function to map MovieDetailsDto to domain MovieDetails model.
 * Includes genre mapping and additional fields specific to details view.
 */
fun MovieDetailsResponse.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview ?: "",
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
        releaseDate = releaseDate?.toFormattedDate() ?: "Unknown",
        rating = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genres.map { it.toGenre() },
        tagline = tagline,
        status = status,
        belongsToCollection = belongsToCollection?.toMovieBelongsToCollection()
    )
}

fun BelongsToCollectionDto.toMovieBelongsToCollection() = MovieBelongsToCollection(
    id = id,
    name = name,
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" }
)

fun CollectionDetailsResponse.toMovieCollectionDetails() = MovieCollectionDetails(
    id = id,
    name = name,
    overview = overview,
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$BACKDROP_SIZE$it" },
    parts = parts.map { it.toMovie() }
)

/**
 * Extension function to map GenreDto to domain Genre model.
 */
fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

/**
 * Extension function to map MovieCreditsDto to domain MovieCredits model.
 * Converts cast and crew members and constructs full profile image URLs.
 */
fun MovieCreditsResponse.toMovieCredits(): MovieCredits {
    return MovieCredits(
        id = id,
        cast = cast.map { it.toCastMember() },
        crew = crew?.map { it.toCrewMember() } ?: emptyList()
    )
}

/**
 * Extension function to map CastMemberDto to domain CastMember model.
 * Constructs full profile image URL.
 */
fun CastMemberDto.toCastMember(): CastMember {
    return CastMember(
        id = id,
        name = name,
        character = character,
        profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" },
        order = order
    )
}

/**
 * Extension function to map CrewMemberDto to domain CrewMember model.
 * Constructs full profile image URL.
 */
fun CrewMemberDto.toCrewMember(): CrewMember {
    return CrewMember(
        id = id,
        name = name,
        job = job,
        department = department,
        profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" }
    )
}

/**
 * Extension function to map ReviewDto to domain Review model.
 * Handles avatar URL construction and rating extraction.
 */
fun ReviewDto.toReview(): Review {
    // TMDB avatar paths can be:
    // 1. Null
    // 2. A relative path like "/abc.jpg"
    // 3. A full Gravatar URL like "https://secure.gravatar.com/..."
    val avatarUrl = authorDetails.avatarPath?.let { path ->
        when {
            path.startsWith("http") -> path // Already full URL
            path.startsWith("/") && path.length > 1 -> {
                // Remove leading slash and check if it's a Gravatar hash
                val cleanPath = path.substring(1)
                if (cleanPath.startsWith("https")) {
                    cleanPath // Gravatar URL without leading slash
                } else {
                    "$IMAGE_BASE_URL$PROFILE_SIZE$path" // TMDB image
                }
            }
            else -> null
        }
    }

    return Review(
        id = id,
        author = author,
        authorUsername = authorDetails.username ?: authorDetails.name ?: author,
        authorAvatarUrl = avatarUrl,
        content = content,
        rating = authorDetails.rating,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ExternalIdsResponse.toExternalIds() = ExternalIds(
    imdbId = imdbId?.takeIf { it.isNotBlank() },
    wikidataId = wikidataId?.takeIf { it.isNotBlank() },
    facebookId = facebookId?.takeIf { it.isNotBlank() },
    instagramId = instagramId?.takeIf { it.isNotBlank() },
    twitterId = twitterId?.takeIf { it.isNotBlank() },
    tvdbId = tvdbId?.takeIf { it.isNotBlank() }
)

private const val BACKDROP_FULL_SIZE = "w1280"

fun MovieImagesResponse.toMovieImages() = MovieImages(
    backdrops = backdrops.map { it.toMovieImage(BACKDROP_FULL_SIZE) },
    posters = posters.map { it.toMovieImage(POSTER_SIZE) }
)

fun MovieVideosResponse.toMovieVideos(): List<MovieVideo> =
    results.filter { it.site == "YouTube" }.map { it.toMovieVideo() }

fun VideoDto.toMovieVideo() = MovieVideo(
    id = id,
    key = key,
    name = name,
    type = type,
    site = site
)

fun MovieKeywordsResponse.toKeywords(): List<Keyword> =
    keywords.map { Keyword(id = it.id, name = it.name) }

fun ImageDto.toMovieImage(size: String) = MovieImage(
    url = "$IMAGE_BASE_URL$size$filePath",
    aspectRatio = aspectRatio,
    width = width,
    height = height
)
