package com.gobidev.tmdbv1.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.usecase.GetMoviePreviewUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieCategoryState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HomeUiState(
    val popular: MovieCategoryState = MovieCategoryState(),
    val nowPlaying: MovieCategoryState = MovieCategoryState(),
    val upcoming: MovieCategoryState = MovieCategoryState()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviePreviewUseCase: GetMoviePreviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(popular = MovieCategoryState(isLoading = true)) }
            when (val result = getMoviePreviewUseCase(MovieListType.POPULAR)) {
                is Result.Success -> _uiState.update {
                    it.copy(popular = MovieCategoryState(movies = result.data))
                }
                is Result.Error -> _uiState.update {
                    it.copy(popular = MovieCategoryState(error = result.message))
                }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(nowPlaying = MovieCategoryState(isLoading = true)) }
            when (val result = getMoviePreviewUseCase(MovieListType.NOW_PLAYING)) {
                is Result.Success -> _uiState.update {
                    it.copy(nowPlaying = MovieCategoryState(movies = result.data))
                }
                is Result.Error -> _uiState.update {
                    it.copy(nowPlaying = MovieCategoryState(error = result.message))
                }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(upcoming = MovieCategoryState(isLoading = true)) }
            when (val result = getMoviePreviewUseCase(MovieListType.UPCOMING)) {
                is Result.Success -> _uiState.update {
                    it.copy(upcoming = MovieCategoryState(movies = result.data))
                }
                is Result.Error -> _uiState.update {
                    it.copy(upcoming = MovieCategoryState(error = result.message))
                }
            }
        }
    }
}
