package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvDetailsUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<TvShowDetails> =
        repository.getTvDetails(tvId)
}
