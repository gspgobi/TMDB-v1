package com.gobidev.tmdbv1.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.usecase.GetPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the popular movies screen.
 *
 * Responsibilities:
 * - Fetch paginated popular movies using the use case
 * - Expose the movies data as StateFlow for the UI to observe
 * - Handle lifecycle-aware data caching with cachedIn(viewModelScope)
 *
 * @param getPopularMoviesUseCase Use case to fetch popular movies
 */
@HiltViewModel
class PopularMoviesViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    // StateFlow for movies PagingData
    private val _movies = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val movies: StateFlow<PagingData<Movie>> = _movies.asStateFlow()

    init {
        loadPopularMovies()
    }

    /**
     * Load popular movies with pagination.
     *
     * cachedIn(viewModelScope) ensures that:
     * - Data survives configuration changes
     * - Paging operations are lifecycle-aware
     * - Previously loaded pages are cached
     */
    private fun loadPopularMovies() {
        viewModelScope.launch {
            getPopularMoviesUseCase()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _movies.value = pagingData
                }
        }
    }
}
