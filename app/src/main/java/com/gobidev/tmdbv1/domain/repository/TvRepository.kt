package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.model.MovieImages
import com.gobidev.tmdbv1.domain.model.MovieVideo
import com.gobidev.tmdbv1.domain.model.TvCredits
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getTvList(type: TvListType): Flow<PagingData<TvShow>>
    suspend fun getTvPreview(type: TvListType): Result<List<TvShow>>
    suspend fun getTvDetails(tvId: Int): Result<TvShowDetails>
    suspend fun getTvCredits(tvId: Int): Result<TvCredits>
    suspend fun getSeasonEpisodes(tvId: Int, seasonNumber: Int): Result<List<Episode>>

    suspend fun getTvExternalIds(tvId: Int): Result<ExternalIds>

    suspend fun getTvImages(tvId: Int): Result<MovieImages>

    suspend fun getTvRecommendations(tvId: Int): Result<List<TvShow>>

    suspend fun getTvVideos(tvId: Int): Result<List<MovieVideo>>
}
