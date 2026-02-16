package com.gobidev.tmdbv1.presentation.util

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
