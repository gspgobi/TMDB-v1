package com.gobidev.tmdbv1.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.usecase.GetMovieDetailsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.presentation.util.MovieDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the movie details screen.
 *
 * Responsibilities:
 * - Fetch detailed movie information based on movie ID
 * - Expose UI state for loading, success, and error scenarios
 * - Handle state transitions clearly using sealed class
 *
 * Uses SavedStateHandle to retrieve navigation arguments (movieId).
 *
 * @param savedStateHandle Handle for accessing navigation arguments
 * @param getMovieDetailsUseCase Use case to fetch movie details
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    // StateFlow for UI state
    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        // Get movieId from navigation arguments
        val movieId = savedStateHandle.get<Int>("movieId") ?: -1
        if (movieId != -1) {
            loadMovieDetails(movieId)
        } else {
            _uiState.value = MovieDetailsUiState.Error("Invalid movie ID")
        }
    }

    /**
     * Load movie details by ID.
     *
     * Updates UI state based on the result:
     * - Loading: Initial state while fetching
     * - Success: Movie details loaded successfully
     * - Error: Failed to load, with error message
     *
     * @param movieId The ID of the movie to load
     */
    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading

            when (val result = getMovieDetailsUseCase(movieId)) {
                is Result.Success -> {
                    _uiState.value = MovieDetailsUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = MovieDetailsUiState.Error(result.message)
                }
            }
        }
    }
}

