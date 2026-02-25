package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching paginated movie reviews.
 * Used for the full reviews screen with infinite scrolling.
 */
class GetMovieReviewsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<PagingData<Review>> {
        return repository.getMovieReviews(movieId)
    }
}
