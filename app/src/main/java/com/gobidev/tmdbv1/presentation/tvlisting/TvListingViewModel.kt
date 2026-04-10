package com.gobidev.tmdbv1.presentation.tvlisting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.domain.model.TvFilterState
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.usecase.GetTvListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TvListingViewModel @Inject constructor(
    private val getTvListUseCase: GetTvListUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tvListType: TvListType = TvListType.fromRouteKey(
        savedStateHandle.get<String>("listType") ?: TvListType.POPULAR.routeKey
    )

    private val keywordId: Int? = savedStateHandle.get<Int>("keywordId")
    val keywordName: String? = savedStateHandle.get<String>("keywordName")
    val isKeywordMode: Boolean = keywordId != null
    val screenTitle: String = if (isKeywordMode) keywordName ?: "TV Shows" else tvListType.title

    private val _filterState = MutableStateFlow(
        if (keywordId != null) TvFilterState(withKeywordId = keywordId) else TvFilterState()
    )
    val filterState: StateFlow<TvFilterState> = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tvShows = _filterState
        .flatMapLatest { filters ->
            getTvListUseCase(tvListType, filters)
        }
        .cachedIn(viewModelScope)

    fun applyFilters(newFilters: TvFilterState) {
        _filterState.value = newFilters
    }

    fun resetFilters() {
        _filterState.value = TvFilterState()
    }
}
