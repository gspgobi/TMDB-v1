package com.gobidev.tmdbv1.presentation.navigation

import android.net.Uri
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.TvListType

/**
 * Parses TMDB web URLs into a synthetic back stack of internal navigation routes.
 *
 * The returned list represents the screens to push on top of Home, from bottom
 * to top. Navigating through them in order produces the correct back stack so
 * that pressing Back always goes to the logical parent screen.
 *
 * Examples:
 *   /movie/155          → [MovieDetails(155)]
 *   /movie/155/cast     → [MovieDetails(155), MovieCast(155)]
 *   /movie/155/reviews  → [MovieDetails(155), MovieReviews(155)]
 *   /tv/1396/cast       → [TvDetails(1396), TvCast(1396)]
 *   /person/6193        → [PersonDetails(6193)]
 *   /movie              → [MovieListing]
 *   /search             → [Search]
 *   /                   → []  (stay on Home)
 *
 * Supported URL patterns:
 *   https://www.themoviedb.org/                           → Home
 *   https://www.themoviedb.org/movie                      → Movie Listing (Popular)
 *   https://www.themoviedb.org/movie/{id}[-slug]          → Movie Details
 *   https://www.themoviedb.org/movie/{id}/cast            → Movie Cast & Crew
 *   https://www.themoviedb.org/movie/{id}/cast_crew       → Movie Cast & Crew
 *   https://www.themoviedb.org/movie/{id}/reviews         → Movie Reviews
 *   https://www.themoviedb.org/tv                         → TV Listing (Popular)
 *   https://www.themoviedb.org/tv/{id}[-slug]             → TV Details
 *   https://www.themoviedb.org/tv/{id}/cast               → TV Cast & Crew
 *   https://www.themoviedb.org/tv/{id}/cast_crew          → TV Cast & Crew
 *   https://www.themoviedb.org/tv/{id}/season/{n}         → TV Details
 *   https://www.themoviedb.org/person/{id}[-slug]         → Person Details
 *   https://www.themoviedb.org/search[?query=…]           → Search
 *
 * @return ordered list of routes to navigate (excluding Home which is always
 *         the start destination), or null if the URL cannot be mapped.
 */
fun parseTmdbUrl(uri: Uri): List<String>? {
    val host = uri.host ?: return null
    if (!host.endsWith("themoviedb.org")) return null

    val segments = uri.pathSegments
    if (segments.isEmpty()) return emptyList()

    return when (segments[0]) {
        "movie"  -> handleMovieUrl(segments)
        "tv"     -> handleTvUrl(segments)
        "person" -> handlePersonUrl(segments)
        "search" -> listOf(Screen.SearchNav.route)
        else     -> null
    }
}

// ── Movie ─────────────────────────────────────────────────────────────────────

private fun handleMovieUrl(segments: List<String>): List<String>? {
    if (segments.size == 1) {
        return listOf(Screen.MovieListingNav.createRoute(MovieListType.POPULAR))
    }

    val movieId = segments[1].parseId() ?: return null
    val detailsRoute = Screen.MovieDetailsNav.createRoute(movieId)

    return when {
        segments.size == 2 ->
            listOf(detailsRoute)
        segments[2] in CAST_SEGMENTS ->
            listOf(detailsRoute, Screen.MovieCastNav.createRoute(movieId, ""))
        segments[2] == "reviews" ->
            listOf(detailsRoute, Screen.MovieReviewsNav.createRoute(movieId, ""))
        else ->
            listOf(detailsRoute)
    }
}

// ── TV ────────────────────────────────────────────────────────────────────────

private fun handleTvUrl(segments: List<String>): List<String>? {
    if (segments.size == 1) {
        return listOf(Screen.TvListingNav.createRoute(TvListType.POPULAR))
    }

    val tvId = segments[1].parseId() ?: return null
    val detailsRoute = Screen.TvDetailsNav.createRoute(tvId)

    return when {
        segments.size == 2 ->
            listOf(detailsRoute)
        segments[2] in CAST_SEGMENTS ->
            listOf(detailsRoute, Screen.TvCastNav.createRoute(tvId, ""))
        // /tv/{id}/season/{n}[/episode/{e}] — TV Details handles season selection
        segments[2] == "season" ->
            listOf(detailsRoute)
        else ->
            listOf(detailsRoute)
    }
}

// ── Person ────────────────────────────────────────────────────────────────────

private fun handlePersonUrl(segments: List<String>): List<String>? {
    if (segments.size < 2) return null
    val personId = segments[1].parseId() ?: return null
    return listOf(Screen.PersonDetailsNav.createRoute(personId))
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** TMDB path segment names that represent the cast & crew page. */
private val CAST_SEGMENTS = setOf("cast", "cast_crew")

/**
 * Parses the leading integer from a TMDB path segment.
 * TMDB slugs look like "155-the-dark-knight"; pure IDs like "155" also work.
 */
private fun String.parseId(): Int? = substringBefore('-').toIntOrNull()
