package com.gobidev.tmdbv1.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gobidev.tmdbv1.data.paging.MovieReviewsPagingSource
import com.gobidev.tmdbv1.data.paging.PopularMoviesPagingSource
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toMovieCredits
import com.gobidev.tmdbv1.data.remote.mapper.toMovieDetails
import com.gobidev.tmdbv1.data.remote.mapper.toReview
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieCredits
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MovieRepository.
 *
 * This is the single source of truth for movie data in the app.
 * It handles data fetching from the API and maps responses to domain models.
 *
 * @param api The TMDB API service
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TMDBApiService
) : MovieRepository {

    /**
     * Get a flow of paginated popular movies using Paging 3.
     *
     * PagingConfig defines:
     * - pageSize: Number of items to load per page
     * - prefetchDistance: How far from the end to start loading the next page
     * - enablePlaceholders: Whether to show placeholders for items not yet loaded
     */
    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // TMDB API returns 20 items per page by default
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularMoviesPagingSource(api) }
        ).flow
    }

    /**
     * Get detailed information for a specific movie.
     *
     * Uses safeCall to wrap the API call and handle exceptions,
     * converting them to Result.Error.
     */
    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> {
        return safeCall {
            val response = api.getMovieDetails(movieId)
            response.toMovieDetails()
        }
    }

    /**
     * Get cast and crew credits for a specific movie.
     *
     * Uses safeCall to wrap the API call and handle exceptions,
     * converting them to Result.Error.
     */
    override suspend fun getMovieCredits(movieId: Int): Result<MovieCredits> {
        return safeCall {
            val response = api.getMovieCredits(movieId)
            response.toMovieCredits()
        }
    }

    /**
     * Get the latest review for a movie (first review from page 1).
     *
     * Returns null if no reviews exist.
     */
    override suspend fun getLatestReview(movieId: Int): Result<Review?> {
        return safeCall {
            val response = api.getMovieReviews(movieId = movieId, page = 1)
            response.results.firstOrNull()?.toReview()
        }
    }

    /**
     * Get a flow of paginated reviews for a movie using Paging 3.
     */
    override fun getMovieReviews(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MovieReviewsPagingSource(api, movieId) }
        ).flow
    }
}
