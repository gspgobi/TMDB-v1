package com.gobidev.tmdbv1.domain.repository

import com.gobidev.tmdbv1.domain.model.TrendingItem
import com.gobidev.tmdbv1.domain.util.Result

interface TrendingRepository {
    suspend fun getTrendingAll(timeWindow: String = "week"): Result<List<TrendingItem>>
}
