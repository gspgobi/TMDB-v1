package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMulti(query: String): Flow<PagingData<SearchResult>>
}
