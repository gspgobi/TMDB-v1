package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.usecase.GetSeasonEpisodesUseCase
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

sealed class EpisodesUiState {
    data object Idle : EpisodesUiState()
    data object Loading : EpisodesUiState()
    data class Success(val episodes: List<Episode>, val totalCount: Int) : EpisodesUiState()
    data class Error(val message: String) : EpisodesUiState()
}

private const val PAGE_SIZE = 10

@HiltViewModel
class TvDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTvDetailsUseCase: GetTvDetailsUseCase,
    private val getTvCreditsUseCase: GetTvCreditsUseCase,
    private val getSeasonEpisodesUseCase: GetSeasonEpisodesUseCase
) : ViewModel() {

    private val tvId = savedStateHandle.get<Int>("tvId") ?: -1

    private val _uiState = MutableStateFlow<TvDetailsUiState>(TvDetailsUiState.Loading)
    val uiState: StateFlow<TvDetailsUiState> = _uiState.asStateFlow()

    private val _castState = MutableStateFlow<TvCastUiState>(TvCastUiState.Loading)
    val castState: StateFlow<TvCastUiState> = _castState.asStateFlow()

    private val _selectedSeasonIndex = MutableStateFlow(0)
    val selectedSeasonIndex: StateFlow<Int> = _selectedSeasonIndex.asStateFlow()

    private val _episodesState = MutableStateFlow<EpisodesUiState>(EpisodesUiState.Idle)
    val episodesState: StateFlow<EpisodesUiState> = _episodesState.asStateFlow()

    private var allEpisodes: List<Episode> = emptyList()
    private var displayedCount: Int = PAGE_SIZE

    init {
        if (tvId != -1) {
            loadDetails()
            loadCredits()
        } else {
            _uiState.value = TvDetailsUiState.Error("Invalid TV show ID")
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            when (val result = getTvDetailsUseCase(tvId)) {
                is Result.Success -> {
                    _uiState.value = TvDetailsUiState.Success(result.data)
                    result.data.seasons.firstOrNull()?.let { firstSeason ->
                        loadEpisodes(firstSeason.seasonNumber)
                    }
                }
                is Result.Error -> _uiState.value = TvDetailsUiState.Error(result.message)
            }
        }
    }

    private fun loadCredits() {
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

    fun selectSeason(index: Int) {
        val seasons = (uiState.value as? TvDetailsUiState.Success)?.tvShow?.seasons ?: return
        val season = seasons.getOrNull(index) ?: return
        _selectedSeasonIndex.value = index
        displayedCount = PAGE_SIZE
        loadEpisodes(season.seasonNumber)
    }

    fun loadMoreEpisodes() {
        displayedCount += PAGE_SIZE
        pushDisplayedEpisodes()
    }

    private fun loadEpisodes(seasonNumber: Int) {
        viewModelScope.launch {
            _episodesState.value = EpisodesUiState.Loading
            when (val result = getSeasonEpisodesUseCase(tvId, seasonNumber)) {
                is Result.Success -> {
                    allEpisodes = result.data
                    pushDisplayedEpisodes()
                }
                is Result.Error -> _episodesState.value = EpisodesUiState.Error(result.message)
            }
        }
    }

    private fun pushDisplayedEpisodes() {
        _episodesState.value = EpisodesUiState.Success(
            episodes = allEpisodes.take(displayedCount),
            totalCount = allEpisodes.size
        )
    }
}
