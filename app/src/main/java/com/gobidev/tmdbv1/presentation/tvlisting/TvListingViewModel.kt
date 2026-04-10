package com.gobidev.tmdbv1.presentation.tvlisting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.usecase.GetTvListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val tvShows = getTvListUseCase(tvListType, keywordId).cachedIn(viewModelScope)
}
