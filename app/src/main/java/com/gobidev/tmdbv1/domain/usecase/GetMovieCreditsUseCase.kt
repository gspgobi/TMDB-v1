package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.MovieCredits
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

/**
 * Use case for fetching movie cast and crew credits.
 *
 * Encapsulates the business logic for retrieving movie credits.
 * The operator fun invoke() allows calling the use case like a function.
 *
 * @param repository The movie repository
 */
class GetMovieCreditsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Invoke the use case to get movie credits.
     *
     * @param movieId The ID of the movie to fetch credits for
     * @return Result containing MovieCredits or an error
     */
    suspend operator fun invoke(movieId: Int): Result<MovieCredits> {
        return repository.getMovieCredits(movieId)
    }
}
