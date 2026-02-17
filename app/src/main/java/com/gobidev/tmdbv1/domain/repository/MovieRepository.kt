package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieDetails
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
     * Get a flow of paginated popular movies.
     *
     * @return Flow emitting PagingData of movies
     */
    fun getPopularMovies(): Flow<PagingData<Movie>>

    /**
     * Get detailed information for a specific movie.
     *
     * @param movieId The ID of the movie
     * @return Result containing MovieDetails or an error
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>
}
