package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

/**
 * Use case for fetching detailed movie information.
 *
 * Encapsulates the business logic for retrieving movie details.
 * The operator fun invoke() allows calling the use case like a function.
 *
 * @param repository The movie repository
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Invoke the use case to get movie details.
     *
     * @param movieId The ID of the movie to fetch
     * @return Result containing MovieDetails or an error
     */
    suspend operator fun invoke(movieId: Int): Result<MovieDetails> {
        return repository.getMovieDetails(movieId)
    }
}
