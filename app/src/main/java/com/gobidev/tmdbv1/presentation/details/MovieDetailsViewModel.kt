package com.gobidev.tmdbv1.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.usecase.GetMovieCreditsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieDetailsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the UI state for movie details screen.
 *
 * Using sealed classes for state management provides:
 * - Type safety: The compiler ensures all states are handled
 * - Clear state transitions: Easy to understand and debug
 * - Composable-friendly: Works well with Compose's recomposition
 */
sealed class MovieDetailsUiState {
    /**
     * Initial state or when loading data.
     */
    data object Loading : MovieDetailsUiState()

    /**
     * Successfully loaded movie details.
     */
    data class Success(val movie: MovieDetails) : MovieDetailsUiState()

    /**
     * An error occurred while loading.
     */
    data class Error(val message: String) : MovieDetailsUiState()
}

/**
 * Sealed class representing the UI state for movie cast.
 *
 * Separate from MovieDetailsUiState to allow independent loading states
 * for movie details and cast information.
 */
sealed class MovieCastUiState {
    /**
     * Initial state or when loading cast data.
     */
    data object Loading : MovieCastUiState()

    /**
     * Successfully loaded cast members.
     */
    data class Success(val cast: List<CastMember>) : MovieCastUiState()

    /**
     * An error occurred while loading cast.
     */
    data class Error(val message: String) : MovieCastUiState()
}

/**
 * ViewModel for the movie details screen.
 *
 * Responsibilities:
 * - Fetch detailed movie information based on movie ID
 * - Fetch cast and crew credits for the movie
 * - Expose UI state for loading, success, and error scenarios
 * - Handle state transitions clearly using sealed classes
 *
 * Uses SavedStateHandle to retrieve navigation arguments (movieId).
 *
 * @param savedStateHandle Handle for accessing navigation arguments
 * @param getMovieDetailsUseCase Use case to fetch movie details
 * @param getMovieCreditsUseCase Use case to fetch movie credits
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase
) : ViewModel() {

    // StateFlow for movie details UI state
    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    // StateFlow for cast UI state
    private val _castState = MutableStateFlow<MovieCastUiState>(MovieCastUiState.Loading)
    val castState: StateFlow<MovieCastUiState> = _castState.asStateFlow()

    init {
        // Get movieId from navigation arguments
        val movieId = savedStateHandle.get<Int>("movieId") ?: -1
        if (movieId != -1) {
            loadMovieDetails(movieId)
            loadMovieCredits(movieId)
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

    /**
     * Load movie cast and crew credits by ID.
     *
     * Updates cast UI state based on the result.
     * Loads independently from movie details to allow parallel loading.
     *
     * @param movieId The ID of the movie to load credits for
     */
    private fun loadMovieCredits(movieId: Int) {
        viewModelScope.launch {
            _castState.value = MovieCastUiState.Loading

            when (val result = getMovieCreditsUseCase(movieId)) {
                is Result.Success -> {
                    // Filter to show only the first 10 cast members for better UI
                    val topCast = result.data.cast
                        .sortedBy { it.order }
                        .take(10)
                    _castState.value = MovieCastUiState.Success(topCast)
                }

                is Result.Error -> {
                    _castState.value = MovieCastUiState.Error(result.message)
                }
            }
        }
    }
}

