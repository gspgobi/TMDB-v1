package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.model.MovieImage
import com.gobidev.tmdbv1.domain.model.MovieVideo
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.usecase.GetSeasonEpisodesUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvCreditsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvDetailsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvExternalIdsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvImagesUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvKeywordsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvRecommendationsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetTvVideosUseCase
import com.gobidev.tmdbv1.presentation.moviedetails.ExternalIdsUiState
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

sealed class TvImagesUiState {
    data object Loading : TvImagesUiState()
    data class Success(val backdrops: List<MovieImage>, val posters: List<MovieImage>) : TvImagesUiState()
    data object Empty : TvImagesUiState()
    data class Error(val message: String) : TvImagesUiState()
}

sealed class TvRecommendationsUiState {
    data object Loading : TvRecommendationsUiState()
    data class Success(val shows: List<TvShow>) : TvRecommendationsUiState()
    data object Empty : TvRecommendationsUiState()
    data class Error(val message: String) : TvRecommendationsUiState()
}

sealed class TvVideosUiState {
    data object Loading : TvVideosUiState()
    data class Success(val videos: List<MovieVideo>) : TvVideosUiState()
    data object Empty : TvVideosUiState()
    data class Error(val message: String) : TvVideosUiState()
}

sealed class TvKeywordsUiState {
    data object Loading : TvKeywordsUiState()
    data class Success(val keywords: List<Keyword>) : TvKeywordsUiState()
    data object Empty : TvKeywordsUiState()
    data class Error(val message: String) : TvKeywordsUiState()
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
    private val getSeasonEpisodesUseCase: GetSeasonEpisodesUseCase,
    private val getTvExternalIdsUseCase: GetTvExternalIdsUseCase,
    private val getTvImagesUseCase: GetTvImagesUseCase,
    private val getTvRecommendationsUseCase: GetTvRecommendationsUseCase,
    private val getTvVideosUseCase: GetTvVideosUseCase,
    private val getTvKeywordsUseCase: GetTvKeywordsUseCase
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

    private val _externalIdsState = MutableStateFlow<ExternalIdsUiState>(ExternalIdsUiState.Loading)
    val externalIdsState: StateFlow<ExternalIdsUiState> = _externalIdsState.asStateFlow()

    private val _imagesState = MutableStateFlow<TvImagesUiState>(TvImagesUiState.Loading)
    val imagesState: StateFlow<TvImagesUiState> = _imagesState.asStateFlow()

    private val _recommendationsState = MutableStateFlow<TvRecommendationsUiState>(TvRecommendationsUiState.Loading)
    val recommendationsState: StateFlow<TvRecommendationsUiState> = _recommendationsState.asStateFlow()

    private val _videosState = MutableStateFlow<TvVideosUiState>(TvVideosUiState.Loading)
    val videosState: StateFlow<TvVideosUiState> = _videosState.asStateFlow()

    private val _keywordsState = MutableStateFlow<TvKeywordsUiState>(TvKeywordsUiState.Loading)
    val keywordsState: StateFlow<TvKeywordsUiState> = _keywordsState.asStateFlow()

    private var allEpisodes: List<Episode> = emptyList()
    private var displayedCount: Int = PAGE_SIZE

    init {
        if (tvId != -1) {
            loadDetails()
            loadCredits()
            loadExternalIds()
            loadImages()
            loadRecommendations()
            loadVideos()
            loadKeywords()
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

    private fun loadExternalIds() {
        viewModelScope.launch {
            when (val result = getTvExternalIdsUseCase(tvId)) {
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

    private fun loadRecommendations() {
        viewModelScope.launch {
            when (val result = getTvRecommendationsUseCase(tvId)) {
                is Result.Success -> {
                    val shows = result.data.take(20)
                    _recommendationsState.value = if (shows.isEmpty()) {
                        TvRecommendationsUiState.Empty
                    } else {
                        TvRecommendationsUiState.Success(shows)
                    }
                }
                is Result.Error -> _recommendationsState.value = TvRecommendationsUiState.Error(result.message)
            }
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            when (val result = getTvVideosUseCase(tvId)) {
                is Result.Success -> {
                    _videosState.value = if (result.data.isEmpty()) {
                        TvVideosUiState.Empty
                    } else {
                        TvVideosUiState.Success(result.data)
                    }
                }
                is Result.Error -> _videosState.value = TvVideosUiState.Error(result.message)
            }
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            when (val result = getTvImagesUseCase(tvId)) {
                is Result.Success -> {
                    val backdrops = result.data.backdrops
                    val posters = result.data.posters
                    if (backdrops.isEmpty() && posters.isEmpty()) {
                        _imagesState.value = TvImagesUiState.Empty
                    } else {
                        _imagesState.value = TvImagesUiState.Success(backdrops, posters)
                    }
                }
                is Result.Error -> _imagesState.value = TvImagesUiState.Error(result.message)
            }
        }
    }

    private fun loadKeywords() {
        viewModelScope.launch {
            when (val result = getTvKeywordsUseCase(tvId)) {
                is Result.Success -> {
                    _keywordsState.value = if (result.data.isEmpty()) {
                        TvKeywordsUiState.Empty
                    } else {
                        TvKeywordsUiState.Success(result.data)
                    }
                }
                is Result.Error -> _keywordsState.value = TvKeywordsUiState.Error(result.message)
            }
        }
    }
}
