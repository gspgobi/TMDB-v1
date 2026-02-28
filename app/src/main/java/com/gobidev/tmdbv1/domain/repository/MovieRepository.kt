package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieCredits
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for movie data operations.
 *
 * Defines the contract for data operations in the domain layer.
 * This abstraction allows the domain layer to be independent of
 * the data layer implementation.
 */
interface MovieRepository {

    /**
     * Get a flow of paginated movies for the given list type and filter state.
     * Routes to the discover endpoint when filters or a non-default sort are active.
     *
     * @param type The list type (popular, now_playing, top_rated, upcoming)
     * @param filters Active filter and sort selections
     * @return Flow emitting PagingData of movies
     */
    fun getMovieList(type: MovieListType, filters: MovieFilterState): Flow<PagingData<Movie>>

    /**
     * Get detailed information for a specific movie.
     *
     * @param movieId The ID of the movie
     * @return Result containing MovieDetails or an error
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>

    /**
     * Get cast and crew credits for a specific movie.
     *
     * @param movieId The ID of the movie
     * @return Result containing MovieCredits or an error
     */
    suspend fun getMovieCredits(movieId: Int): Result<MovieCredits>

    /**
     * Get a single review for a movie (latest review).
     *
     * @param movieId The ID of the movie
     * @return Result containing a Review or an error
     */
    suspend fun getLatestReview(movieId: Int): Result<Review?>

    /**
     * Get a flow of paginated reviews for a movie.
     *
     * @param movieId The ID of the movie
     * @return Flow emitting PagingData of reviews
     */
    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>
}
