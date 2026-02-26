package com.gobidev.tmdbv1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toReview
import com.gobidev.tmdbv1.domain.model.Review
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource implementation for movie reviews.
 *
 * Handles pagination logic for infinite scrolling of reviews.
 * Fetches pages of reviews from the TMDB API and converts them to domain models.
 *
 * @param api The TMDB API service
 * @param movieId The ID of the movie to fetch reviews for
 */
class MovieReviewsPagingSource(
    private val api: TMDBApiService,
    private val movieId: Int
) : PagingSource<Int, Review>() {

    /**
     * Load a page of reviews.
     *
     * @param params Contains the page key to load
     * @return LoadResult containing the data or an error
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            // Get the page number, default to 1 for initial load
            val page = params.key ?: 1

            // Fetch reviews from API
            val response = api.getMovieReviews(movieId = movieId, page = page)

            // Convert DTOs to domain models
            val reviews = response.results.map { it.toReview() }

            // Return successful result with pagination keys
            LoadResult.Page(
                data = reviews,
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
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        // Try to find the page key of the closest page to the current scroll position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
