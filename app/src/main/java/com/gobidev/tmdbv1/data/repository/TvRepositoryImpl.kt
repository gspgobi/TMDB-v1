package com.gobidev.tmdbv1.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gobidev.tmdbv1.data.paging.TvListPagingSource
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toEpisode
import com.gobidev.tmdbv1.data.remote.mapper.toExternalIds
import com.gobidev.tmdbv1.data.remote.mapper.toMovieImages
import com.gobidev.tmdbv1.data.remote.mapper.toKeywords
import com.gobidev.tmdbv1.data.remote.mapper.toMovieVideos
import com.gobidev.tmdbv1.data.remote.mapper.toTvCredits
import com.gobidev.tmdbv1.data.remote.mapper.toTvDetails
import com.gobidev.tmdbv1.data.remote.mapper.toTvShow
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.model.MovieImages
import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.model.MovieVideo
import com.gobidev.tmdbv1.domain.model.TvCredits
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.domain.repository.TvRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepositoryImpl @Inject constructor(
    private val api: TMDBApiService
) : TvRepository {

    override fun getTvList(type: TvListType): Flow<PagingData<TvShow>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false),
            pagingSourceFactory = { TvListPagingSource(api, type) }
        ).flow
    }

    override suspend fun getTvPreview(type: TvListType): Result<List<TvShow>> = safeCall {
        val response = when (type) {
            TvListType.POPULAR -> api.getPopularTv(page = 1)
            TvListType.TOP_RATED -> api.getTopRatedTv(page = 1)
            TvListType.ON_THE_AIR -> api.getOnTheAirTv(page = 1)
            TvListType.AIRING_TODAY -> api.getAiringTodayTv(page = 1)
        }
        response.results.map { it.toTvShow() }
    }

    override suspend fun getTvDetails(tvId: Int): Result<TvShowDetails> = safeCall {
        api.getTvDetails(tvId).toTvDetails()
    }

    override suspend fun getTvCredits(tvId: Int): Result<TvCredits> = safeCall {
        api.getTvCredits(tvId).toTvCredits()
    }

    override suspend fun getSeasonEpisodes(tvId: Int, seasonNumber: Int): Result<List<Episode>> = safeCall {
        api.getSeasonDetails(tvId, seasonNumber).episodes.map { it.toEpisode() }
    }

    override suspend fun getTvExternalIds(tvId: Int): Result<ExternalIds> =
        safeCall { api.getTvExternalIds(tvId).toExternalIds() }

    override suspend fun getTvImages(tvId: Int): Result<MovieImages> =
        safeCall { api.getTvImages(tvId).toMovieImages() }

    override suspend fun getTvRecommendations(tvId: Int): Result<List<TvShow>> = safeCall {
        api.getTvRecommendations(tvId).results.map { it.toTvShow() }
    }

    override suspend fun getTvVideos(tvId: Int): Result<List<MovieVideo>> =
        safeCall { api.getTvVideos(tvId).toMovieVideos() }

    override suspend fun getTvKeywords(tvId: Int): Result<List<Keyword>> =
        safeCall { api.getTvKeywords(tvId).toKeywords() }
}
