package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching popular movies.
 *
 * Use cases encapsulate business logic and provide a clear API for the presentation layer.
 * They act as an abstraction between the repository and ViewModels.
 *
 * This use case is simple as it just delegates to the repository,
 * but in more complex scenarios, use cases can combine data from multiple repositories,
 * apply business rules, or perform data transformations.
 *
 * @param repository The movie repository
 */
class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Invoke the use case to get paginated popular movies.
     *
     * @return Flow of PagingData containing movies
     */
    operator fun invoke(): Flow<PagingData<Movie>> {
        return repository.getPopularMovies()
    }
}
