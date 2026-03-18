package com.gobidev.tmdbv1.data.repository

import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toTrendingItem
import com.gobidev.tmdbv1.domain.model.TrendingItem
import com.gobidev.tmdbv1.domain.repository.TrendingRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrendingRepositoryImpl @Inject constructor(
    private val api: TMDBApiService
) : TrendingRepository {

    override suspend fun getTrendingAll(timeWindow: String): Result<List<TrendingItem>> = safeCall {
        api.getTrendingAll(timeWindow).results.mapNotNull { it.toTrendingItem() }
    }
}
