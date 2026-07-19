package com.gobidev.tmdbv1.domain.usecase

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistTvUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<PagingData<TvShow>> =
        repository.getWatchlistTvShows()
}
