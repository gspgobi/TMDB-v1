package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvExternalIdsUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<ExternalIds> =
        repository.getTvExternalIds(tvId)
}
