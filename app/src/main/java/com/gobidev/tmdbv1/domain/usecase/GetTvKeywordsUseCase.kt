package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvKeywordsUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<List<Keyword>> =
        repository.getTvKeywords(tvId)
}
