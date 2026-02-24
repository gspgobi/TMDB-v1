package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

/**
 * Use case for fetching the latest review for a movie.
 * Used to display a single review preview in the movie details screen.
 */
class GetLatestReviewUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Review?> {
        return repository.getLatestReview(movieId)
    }
}
