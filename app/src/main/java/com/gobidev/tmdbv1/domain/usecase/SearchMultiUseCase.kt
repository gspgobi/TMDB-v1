package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.SearchResult
import com.gobidev.tmdbv1.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMultiUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    operator fun invoke(query: String): Flow<PagingData<SearchResult>> =
        repository.searchMulti(query)
}
