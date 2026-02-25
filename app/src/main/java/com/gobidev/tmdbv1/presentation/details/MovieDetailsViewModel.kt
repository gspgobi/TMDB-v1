package com.gobidev.tmdbv1.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.usecase.GetLatestReviewUseCase
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
    data object Loading : MovieDetailsUiState()
    data class Success(val movie: MovieDetails) : MovieDetailsUiState()
    data class Error(val message: String) : MovieDetailsUiState()
}

sealed class MovieCastUiState {
    data object Loading : MovieCastUiState()
    data class Success(val cast: List<CastMember>) : MovieCastUiState()
    data class Error(val message: String) : MovieCastUiState()
}

sealed class MovieReviewUiState {
    data object Loading : MovieReviewUiState()
    data class Success(val review: Review) : MovieReviewUiState()
    data object NoReviews : MovieReviewUiState()
    data class Error(val message: String) : MovieReviewUiState()
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
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getLatestReviewUseCase: GetLatestReviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val _castState = MutableStateFlow<MovieCastUiState>(MovieCastUiState.Loading)
    val castState: StateFlow<MovieCastUiState> = _castState.asStateFlow()

    private val _reviewState = MutableStateFlow<MovieReviewUiState>(MovieReviewUiState.Loading)
    val reviewState: StateFlow<MovieReviewUiState> = _reviewState.asStateFlow()

    init {
        val movieId = savedStateHandle.get<Int>("movieId") ?: -1
        if (movieId != -1) {
            loadMovieDetails(movieId)
            loadMovieCredits(movieId)
            loadLatestReview(movieId)  // ADD THIS
        } else {
            _uiState.value = MovieDetailsUiState.Error("Invalid movie ID")
        }
    }

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

    private fun loadLatestReview(movieId: Int) {
        viewModelScope.launch {
            _reviewState.value = MovieReviewUiState.Loading

            when (val result = getLatestReviewUseCase(movieId)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _reviewState.value = MovieReviewUiState.Success(result.data)
                    } else {
                        _reviewState.value = MovieReviewUiState.NoReviews
                    }
                }

                is Result.Error -> {
                    _reviewState.value = MovieReviewUiState.Error(result.message)
                }
            }
        }
    }
}

