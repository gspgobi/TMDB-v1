package com.gobidev.tmdbv1.presentation.movielisting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.usecase.GetMovieListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel for [MovieListingScreen].
 *
 * Exposes a [movies] flow that automatically re-paginate whenever [filterState] changes.
 * [flatMapLatest] ensures the previous collection is cancelled before starting a new one.
 *
 * @param getMovieListUseCase Use case for fetching a paginated movie list
 * @param savedStateHandle Provides navigation arguments (listType route param)
 */
@HiltViewModel
class MovieListingViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val movieListType: MovieListType = MovieListType.fromRouteKey(
        savedStateHandle.get<String>("listType") ?: MovieListType.POPULAR.routeKey
    )

    private val _filterState = MutableStateFlow(MovieFilterState())
    val filterState: StateFlow<MovieFilterState> = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val movies = _filterState
        .flatMapLatest { filters ->
            getMovieListUseCase(movieListType, filters)
        }
        .cachedIn(viewModelScope)

    fun applyFilters(newFilters: MovieFilterState) {
        _filterState.value = newFilters
    }

    fun resetFilters() {
        _filterState.value = MovieFilterState()
    }
}