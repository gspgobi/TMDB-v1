package com.gobidev.tmdbv1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toMovie
import com.gobidev.tmdbv1.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource implementation for popular movies.
 *
 * This handles the pagination logic for infinite scrolling.
 * Fetches pages of movies from the TMDB API and converts them to domain models.
 *
 * @param api The TMDB API service
 */
class PopularMoviesPagingSource(
    private val api: TMDBApiService
) : PagingSource<Int, Movie>() {

    /**
     * Load a page of movies.
     *
     * @param params Contains the page key to load
     * @return LoadResult containing the data or an error
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            // Get the page number, default to 1 for initial load
            val page = params.key ?: 1

            // Fetch movies from API
            val response = api.getPopularMovies(page = page)

            // Convert DTOs to domain models
            val movies = response.results.map { it.toMovie() }

            // Return successful result with pagination keys
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: IOException) {
            // Handle network errors
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // Handle HTTP errors
            LoadResult.Error(e)
        } catch (e: Exception) {
            // Handle unexpected errors
            LoadResult.Error(e)
        }
    }

    /**
     * Provide a key to use for the initial load.
     * This is called when the PagingSource is invalidated.
     */
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Try to find the page key of the closest page to the current scroll position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
