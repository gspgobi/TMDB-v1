package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching a paginated movie list.
 *
 * Delegates routing logic (natural endpoint vs discover) to the repository/paging source.
 *
 * @param repository Movie data repository
 */
class GetMovieListUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(
        listType: MovieListType,
        filters: MovieFilterState
    ): Flow<PagingData<Movie>> = repository.getMovieList(listType, filters)
}