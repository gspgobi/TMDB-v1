package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvPreviewUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(type: TvListType): Result<List<TvShow>> =
        repository.getTvPreview(type)
}
