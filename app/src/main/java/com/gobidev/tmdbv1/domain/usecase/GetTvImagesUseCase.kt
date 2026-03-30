package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.MovieImages
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvImagesUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<MovieImages> =
        repository.getTvImages(tvId)
}
