package com.gobidev.tmdbv1.presentation.util

import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.MovieDetails


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

    /**
     * Cast data is not yet requested or needed.
     */
    data object Idle : MovieCastUiState()
}
