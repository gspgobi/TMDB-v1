package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.domain.usecase.GetTvCreditsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TvFullCastUiState {
    data object Loading : TvFullCastUiState()
    data class Success(val cast: List<CastMember>, val crew: List<CrewMember>) : TvFullCastUiState()
    data class Error(val message: String) : TvFullCastUiState()
}

@HiltViewModel
class TvFullCastCrewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTvCreditsUseCase: GetTvCreditsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TvFullCastUiState>(TvFullCastUiState.Loading)
    val uiState: StateFlow<TvFullCastUiState> = _uiState.asStateFlow()

    val tvName: String = savedStateHandle.get<String>("tvName") ?: "TV Show"
    private val tvId: Int = savedStateHandle.get<Int>("tvId") ?: -1

    init {
        if (tvId != -1) loadCredits(tvId)
        else _uiState.value = TvFullCastUiState.Error("Invalid TV show ID")
    }

    private fun loadCredits(tvId: Int) {
        viewModelScope.launch {
            _uiState.value = TvFullCastUiState.Loading
            when (val result = getTvCreditsUseCase(tvId)) {
                is Result.Success -> {
                    val credits = result.data
                    val sortedCast = credits.cast.sortedBy { it.order }
                    val sortedCrew = credits.crew.sortedWith(
                        compareBy<CrewMember> { member ->
                            when (member.job) {
                                "Director" -> 0
                                "Writer", "Screenplay" -> 1
                                "Producer", "Executive Producer" -> 2
                                else -> 3
                            }
                        }.thenBy { it.name }
                    )
                    _uiState.value = TvFullCastUiState.Success(cast = sortedCast, crew = sortedCrew)
                }
                is Result.Error -> _uiState.value = TvFullCastUiState.Error(result.message)
            }
        }
    }

    fun retry() {
        if (tvId != -1) loadCredits(tvId)
    }
}
