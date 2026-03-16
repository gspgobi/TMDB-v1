package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetSeasonEpisodesUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int, seasonNumber: Int): Result<List<Episode>> =
        repository.getSeasonEpisodes(tvId, seasonNumber)
}
