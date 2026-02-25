package com.gobidev.tmdbv1.presentation.reviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.domain.usecase.GetMovieReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the movie reviews screen.
 * Fetches and manages paginated reviews for a specific movie.
 */
@HiltViewModel
class MovieReviewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase
) : ViewModel() {

    // Movie title from navigation
    val movieTitle: String = savedStateHandle.get<String>("movieTitle") ?: "Movie"

    // StateFlow for reviews PagingData
    private val _reviews = MutableStateFlow<PagingData<Review>>(PagingData.empty())
    val reviews: StateFlow<PagingData<Review>> = _reviews.asStateFlow()

    init {
        val movieId = savedStateHandle.get<Int>("movieId") ?: -1
        if (movieId != -1) {
            loadReviews(movieId)
        }
    }

    private fun loadReviews(movieId: Int) {
        viewModelScope.launch {
            getMovieReviewsUseCase(movieId)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _reviews.value = pagingData
                }
        }
    }
}
