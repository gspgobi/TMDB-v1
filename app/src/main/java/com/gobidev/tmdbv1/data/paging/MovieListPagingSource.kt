package com.gobidev.tmdbv1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toMovie
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.MovieSortOption
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource for the common MovieListingScreen.
 *
 * Routes API calls based on the active [filters]:
 * - No filters / no explicit sort → natural endpoint (popular, now_playing, top_rated, upcoming)
 * - Any filter or sort active → discover/movie endpoint with full query params
 *
 * @param api TMDB API service
 * @param listType The base list type that determines the natural endpoint and default sort
 * @param filters Current filter and sort state
 */
class MovieListPagingSource(
    private val api: TMDBApiService,
    private val listType: MovieListType,
    private val filters: MovieFilterState
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1

            val response = if (filters.needsDiscoverApi) {
                val sortBy = filters.sortBy?.apiValue ?: listType.defaultSortApiValue()
                val withGenres = filters.selectedGenreIds.joinToString("|").ifEmpty { null }
                val minRating = if (filters.minRating > 0f) filters.minRating.toDouble() else null

                api.discoverMovies(
                    page = page,
                    sortBy = sortBy,
                    withGenres = withGenres,
                    voteAverageGte = minRating,
                    primaryReleaseYear = filters.releaseYear
                )
            } else {
                when (listType) {
                    MovieListType.POPULAR -> api.getPopularMovies(page = page)
                    MovieListType.NOW_PLAYING -> api.getNowPlayingMovies(page = page)
                    MovieListType.TOP_RATED -> api.getTopRatedMovies(page = page)
                    MovieListType.UPCOMING -> api.getUpcomingMovies(page = page)
                }
            }

            LoadResult.Page(
                data = response.results.map { it.toMovie() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

private fun MovieListType.defaultSortApiValue(): String = when (this) {
    MovieListType.POPULAR -> MovieSortOption.POPULARITY_DESC.apiValue
    MovieListType.NOW_PLAYING -> "release_date.desc"
    MovieListType.TOP_RATED -> MovieSortOption.RATING_DESC.apiValue
    MovieListType.UPCOMING -> "release_date.asc"
}