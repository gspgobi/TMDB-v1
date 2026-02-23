package com.gobidev.tmdbv1.presentation.castcrew

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.usecase.GetMovieCreditsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * UI state for full cast & crew screen.
 */
sealed class FullCastCrewUiState {
    data object Loading : FullCastCrewUiState()
    data class Success(
        val cast: List<CastMember>,
        val crew: List<CrewMember>
    ) : FullCastCrewUiState()

    data class Error(val message: String) : FullCastCrewUiState()
}

/**
 * ViewModel for the full cast & crew screen.
 *
 * Responsibilities:
 * - Fetch complete cast and crew credits for a movie
 * - Expose UI state for the screen
 * - Handle loading and error states
 *
 * @param savedStateHandle Handle for accessing navigation arguments
 * @param getMovieCreditsUseCase Use case to fetch movie credits
 */
@HiltViewModel
class FullCastCrewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase
) : ViewModel() {

    // StateFlow for UI state
    private val _uiState = MutableStateFlow<FullCastCrewUiState>(FullCastCrewUiState.Loading)
    val uiState: StateFlow<FullCastCrewUiState> = _uiState.asStateFlow()

    // Movie title from navigation - query params handle special characters automatically
    val movieTitle: String = savedStateHandle.get<String>("movieTitle") ?: "Movie"

    // Store movieId for retry
    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    init {
        // Get movieId from navigation arguments
        if (movieId != -1) {
            loadFullCredits(movieId)
        } else {
            _uiState.value = FullCastCrewUiState.Error("Invalid movie ID")
        }
    }

    /**
     * Load full cast and crew credits for the movie.
     *
     * Unlike the details screen which shows only top 10 cast,
     * this loads and displays the complete cast and crew list.
     *
     * @param movieId The ID of the movie to load credits for
     */
    private fun loadFullCredits(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = FullCastCrewUiState.Loading

            when (val result = getMovieCreditsUseCase(movieId)) {
                is Result.Success -> {
                    val credits = result.data

                    // Sort cast by order (billing order)
                    val sortedCast = credits.cast.sortedBy { it.order }

                    // Group crew by department and sort by job importance
                    val sortedCrew = credits.crew.sortedWith(
                        compareBy<CrewMember> { member ->
                            // Prioritize key roles
                            when (member.job) {
                                "Director" -> 0
                                "Writer", "Screenplay" -> 1
                                "Producer", "Executive Producer" -> 2
                                else -> 3
                            }
                        }.thenBy { it.name }
                    )

                    _uiState.value = FullCastCrewUiState.Success(
                        cast = sortedCast,
                        crew = sortedCrew
                    )
                }

                is Result.Error -> {
                    _uiState.value = FullCastCrewUiState.Error(result.message)
                }
            }
        }
    }

    /**
     * Retry loading credits after an error.
     */
    fun retry() {
        if (movieId != -1) {
            loadFullCredits(movieId)
        }
    }
}
