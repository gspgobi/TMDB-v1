package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.TvCredits
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getTvList(type: TvListType): Flow<PagingData<TvShow>>
    suspend fun getTvPreview(type: TvListType): Result<List<TvShow>>
    suspend fun getTvDetails(tvId: Int): Result<TvShowDetails>
    suspend fun getTvCredits(tvId: Int): Result<TvCredits>
}
