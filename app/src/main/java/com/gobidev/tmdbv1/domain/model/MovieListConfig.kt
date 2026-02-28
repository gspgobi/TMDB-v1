package com.gobidev.tmdbv1.domain.model

/**
 * Identifies which TMDB movie list endpoint to use.
 */
enum class MovieListType(val title: String, val routeKey: String) {
    POPULAR("Popular Movies", "popular"),
    NOW_PLAYING("Now Playing", "now_playing"),
    TOP_RATED("Top Rated", "top_rated"),
    UPCOMING("Upcoming", "upcoming");

    companion object {
        fun fromRouteKey(key: String): MovieListType =
            entries.find { it.routeKey == key } ?: POPULAR
    }
}

/**
 * Sort options supported by the TMDB discover/movie endpoint.
 */
enum class MovieSortOption(val apiValue: String, val displayName: String) {
    POPULARITY_DESC("popularity.desc", "Most Popular"),
    RATING_DESC("vote_average.desc", "Highest Rated"),
    RELEASE_DATE_DESC("release_date.desc", "Newest First"),
    RELEASE_DATE_ASC("release_date.asc", "Oldest First"),
    VOTE_COUNT_DESC("vote_count.desc", "Most Voted")
}

/**
 * A TMDB movie genre with its ID and display name.
 * IDs are stable and defined by TMDB.
 */
data class GenreItem(val id: Int, val name: String) {
    companion object {
        val ALL_GENRES = listOf(
            GenreItem(28, "Action"),
            GenreItem(12, "Adventure"),
            GenreItem(16, "Animation"),
            GenreItem(35, "Comedy"),
            GenreItem(80, "Crime"),
            GenreItem(99, "Documentary"),
            GenreItem(18, "Drama"),
            GenreItem(10751, "Family"),
            GenreItem(14, "Fantasy"),
            GenreItem(36, "History"),
            GenreItem(27, "Horror"),
            GenreItem(10402, "Music"),
            GenreItem(9648, "Mystery"),
            GenreItem(10749, "Romance"),
            GenreItem(878, "Science Fiction"),
            GenreItem(10770, "TV Movie"),
            GenreItem(53, "Thriller"),
            GenreItem(10752, "War"),
            GenreItem(37, "Western")
        )
    }
}

/**
 * Holds the current filter and sort selections for the movie listing.
 *
 * When [needsDiscoverApi] is true the repository will route to the
 * discover/movie endpoint instead of the natural list endpoint.
 */
data class MovieFilterState(
    val sortBy: MovieSortOption? = null,
    val selectedGenreIds: Set<Int> = emptySet(),
    val minRating: Float = 0f,
    val releaseYear: Int? = null
) {
    /** True when the discover endpoint is required to satisfy the current state. */
    val needsDiscoverApi: Boolean
        get() = sortBy != null || selectedGenreIds.isNotEmpty() || minRating > 0f || releaseYear != null

    /** Count of active filter selections (excludes sort). */
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedGenreIds.isNotEmpty()) count++
            if (minRating > 0f) count++
            if (releaseYear != null) count++
            return count
        }

    /** True when a non-default sort is selected. */
    val isSortActive: Boolean
        get() = sortBy != null
}