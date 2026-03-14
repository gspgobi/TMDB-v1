package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.usecase.GetTvCreditsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvDetailsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TvDetailsUiState {
    data object Loading : TvDetailsUiState()
    data class Success(val tvShow: TvShowDetails) : TvDetailsUiState()
    data class Error(val message: String) : TvDetailsUiState()
}

sealed class TvCastUiState {
    data object Loading : TvCastUiState()
    data class Success(val cast: List<CastMember>) : TvCastUiState()
    data class Error(val message: String) : TvCastUiState()
}

@HiltViewModel
class TvDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTvDetailsUseCase: GetTvDetailsUseCase,
    private val getTvCreditsUseCase: GetTvCreditsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvDetailsUiState>(TvDetailsUiState.Loading)
    val uiState: StateFlow<TvDetailsUiState> = _uiState.asStateFlow()

    private val _castState = MutableStateFlow<TvCastUiState>(TvCastUiState.Loading)
    val castState: StateFlow<TvCastUiState> = _castState.asStateFlow()

    init {
        val tvId = savedStateHandle.get<Int>("tvId") ?: -1
        if (tvId != -1) {
            loadDetails(tvId)
            loadCredits(tvId)
        } else {
            _uiState.value = TvDetailsUiState.Error("Invalid TV show ID")
        }
    }

    private fun loadDetails(tvId: Int) {
        viewModelScope.launch {
            when (val result = getTvDetailsUseCase(tvId)) {
                is Result.Success -> _uiState.value = TvDetailsUiState.Success(result.data)
                is Result.Error -> _uiState.value = TvDetailsUiState.Error(result.message)
            }
        }
    }

    private fun loadCredits(tvId: Int) {
        viewModelScope.launch {
            when (val result = getTvCreditsUseCase(tvId)) {
                is Result.Success -> {
                    val topCast = result.data.cast.sortedBy { it.order }.take(10)
                    _castState.value = TvCastUiState.Success(topCast)
                }
                is Result.Error -> _castState.value = TvCastUiState.Error(result.message)
            }
        }
    }
}
