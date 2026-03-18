package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.TrendingItem
import com.gobidev.tmdbv1.domain.repository.TrendingRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTrendingUseCase @Inject constructor(
    private val repository: TrendingRepository
) {
    suspend operator fun invoke(): Result<List<TrendingItem>> =
        repository.getTrendingAll()
}
