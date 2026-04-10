package com.gobidev.tmdbv1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toTvShow
import com.gobidev.tmdbv1.domain.model.MovieSortOption
import com.gobidev.tmdbv1.domain.model.TvFilterState
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.domain.model.TvShow
import retrofit2.HttpException
import java.io.IOException

class TvListPagingSource(
    private val api: TMDBApiService,
    private val listType: TvListType,
    private val filters: TvFilterState = TvFilterState()
) : PagingSource<Int, TvShow>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShow> {
        return try {
            val page = params.key ?: 1
            val response = if (filters.needsDiscoverApi) {
                val sortBy = filters.sortBy?.apiValue ?: listType.defaultSortApiValue()
                val withGenres = filters.selectedGenreIds.joinToString("|").ifEmpty { null }
                val minRating = if (filters.minRating > 0f) filters.minRating.toDouble() else null
                api.discoverTv(
                    page = page,
                    sortBy = sortBy,
                    withGenres = withGenres,
                    voteAverageGte = minRating,
                    firstAirDateYear = filters.firstAirYear,
                    withKeywords = filters.withKeywordId
                )
            } else {
                when (listType) {
                    TvListType.POPULAR -> api.getPopularTv(page = page)
                    TvListType.TOP_RATED -> api.getTopRatedTv(page = page)
                    TvListType.ON_THE_AIR -> api.getOnTheAirTv(page = page)
                    TvListType.AIRING_TODAY -> api.getAiringTodayTv(page = page)
                }
            }
            LoadResult.Page(
                data = response.results.map { it.toTvShow() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TvShow>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
