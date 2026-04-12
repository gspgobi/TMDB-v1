package com.gobidev.tmdbv1.presentation.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.usecase.GetLatestReviewUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieCreditsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieDetailsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieExternalIdsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieImagesUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieKeywordsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieRecommendationsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetMovieVideosUseCase
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.presentation.components.CastUiState
import com.gobidev.tmdbv1.presentation.components.ExternalIdsUiState
import com.gobidev.tmdbv1.presentation.components.ImagesUiState
import com.gobidev.tmdbv1.presentation.components.KeywordsUiState
import com.gobidev.tmdbv1.presentation.components.VideosUiState
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

sealed class MovieReviewUiState {
    data object Loading : MovieReviewUiState()
    data class Success(val review: Review) : MovieReviewUiState()
    data object NoReviews : MovieReviewUiState()
    data class Error(val message: String) : MovieReviewUiState()
}

sealed class MovieRecommendationsUiState {
    data object Loading : MovieRecommendationsUiState()
    data class Success(val movies: List<Movie>) : MovieRecommendationsUiState()
    data object Empty : MovieRecommendationsUiState()
    data class Error(val message: String) : MovieRecommendationsUiState()
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
    private val getLatestReviewUseCase: GetLatestReviewUseCase,
    private val getMovieRecommendationsUseCase: GetMovieRecommendationsUseCase,
    private val getMovieExternalIdsUseCase: GetMovieExternalIdsUseCase,
    private val getMovieImagesUseCase: GetMovieImagesUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val getMovieKeywordsUseCase: GetMovieKeywordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val _castState = MutableStateFlow<CastUiState>(CastUiState.Loading)
    val castState: StateFlow<CastUiState> = _castState.asStateFlow()

    private val _reviewState = MutableStateFlow<MovieReviewUiState>(MovieReviewUiState.Loading)
    val reviewState: StateFlow<MovieReviewUiState> = _reviewState.asStateFlow()

    private val _recommendationsState = MutableStateFlow<MovieRecommendationsUiState>(MovieRecommendationsUiState.Loading)
    val recommendationsState: StateFlow<MovieRecommendationsUiState> = _recommendationsState.asStateFlow()

    private val _externalIdsState = MutableStateFlow<ExternalIdsUiState>(ExternalIdsUiState.Loading)
    val externalIdsState: StateFlow<ExternalIdsUiState> = _externalIdsState.asStateFlow()

    private val _imagesState = MutableStateFlow<ImagesUiState>(ImagesUiState.Loading)
    val imagesState: StateFlow<ImagesUiState> = _imagesState.asStateFlow()

    private val _videosState = MutableStateFlow<VideosUiState>(VideosUiState.Loading)
    val videosState: StateFlow<VideosUiState> = _videosState.asStateFlow()

    private val _keywordsState = MutableStateFlow<KeywordsUiState>(KeywordsUiState.Loading)
    val keywordsState: StateFlow<KeywordsUiState> = _keywordsState.asStateFlow()

    init {
        val movieId = savedStateHandle.get<Int>("movieId") ?: -1
        if (movieId != -1) {
            loadMovieDetails(movieId)
            loadMovieCredits(movieId)
            loadLatestReview(movieId)
            loadRecommendations(movieId)
            loadExternalIds(movieId)
            loadImages(movieId)
            loadVideos(movieId)
            loadKeywords(movieId)
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
            _castState.value = CastUiState.Loading

            when (val result = getMovieCreditsUseCase(movieId)) {
                is Result.Success -> {
                    // Filter to show only the first 10 cast members for better UI
                    val topCast = result.data.cast
                        .sortedBy { it.order }
                        .take(10)
                    _castState.value = CastUiState.Success(topCast)
                }

                is Result.Error -> {
                    _castState.value = CastUiState.Error(result.message)
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

    private fun loadRecommendations(movieId: Int) {
        viewModelScope.launch {
            _recommendationsState.value = MovieRecommendationsUiState.Loading

            when (val result = getMovieRecommendationsUseCase(movieId)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _recommendationsState.value = MovieRecommendationsUiState.Empty
                    } else {
                        _recommendationsState.value = MovieRecommendationsUiState.Success(result.data)
                    }
                }

                is Result.Error -> {
                    _recommendationsState.value = MovieRecommendationsUiState.Error(result.message)
                }
            }
        }
    }

    private fun loadExternalIds(movieId: Int) {
        viewModelScope.launch {
            when (val result = getMovieExternalIdsUseCase(movieId)) {
                is Result.Success -> {
                    if (result.data.hasAny()) {
                        _externalIdsState.value = ExternalIdsUiState.Success(result.data)
                    } else {
                        _externalIdsState.value = ExternalIdsUiState.Empty
                    }
                }
                is Result.Error -> _externalIdsState.value = ExternalIdsUiState.Error(result.message)
            }
        }
    }

    private fun loadImages(movieId: Int) {
        viewModelScope.launch {
            when (val result = getMovieImagesUseCase(movieId)) {
                is Result.Success -> {
                    val backdrops = result.data.backdrops
                    val posters = result.data.posters
                    if (backdrops.isEmpty() && posters.isEmpty()) {
                        _imagesState.value = ImagesUiState.Empty
                    } else {
                        _imagesState.value = ImagesUiState.Success(backdrops, posters)
                    }
                }
                is Result.Error -> _imagesState.value = ImagesUiState.Error(result.message)
            }
        }
    }

    private fun loadVideos(movieId: Int) {
        viewModelScope.launch {
            when (val result = getMovieVideosUseCase(movieId)) {
                is Result.Success -> {
                    _videosState.value = if (result.data.isEmpty()) {
                        VideosUiState.Empty
                    } else {
                        VideosUiState.Success(result.data)
                    }
                }
                is Result.Error -> _videosState.value = VideosUiState.Error(result.message)
            }
        }
    }

    private fun loadKeywords(movieId: Int) {
        viewModelScope.launch {
            when (val result = getMovieKeywordsUseCase(movieId)) {
                is Result.Success -> {
                    _keywordsState.value = if (result.data.isEmpty()) {
                        KeywordsUiState.Empty
                    } else {
                        KeywordsUiState.Success(result.data)
                    }
                }
                is Result.Error -> _keywordsState.value = KeywordsUiState.Error(result.message)
            }
        }
    }
}

