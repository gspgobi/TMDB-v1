package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.repository.TvRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTvListUseCase @Inject constructor(
    private val repository: TvRepository
) {
    operator fun invoke(type: TvListType, withKeywordId: Int? = null): Flow<PagingData<TvShow>> =
        repository.getTvList(type, withKeywordId)
}
