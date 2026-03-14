package com.gobidev.tmdbv1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toMovie
import com.gobidev.tmdbv1.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

enum class AccountMovieListType { FAVORITES, WATCHLIST }

class AccountMoviesPagingSource(
    private val api: TMDBApiService,
    private val accountId: Int,
    private val sessionId: String,
    private val listType: AccountMovieListType
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = when (listType) {
                AccountMovieListType.FAVORITES ->
                    api.getFavoriteMovies(accountId = accountId, sessionId = sessionId, page = page)
                AccountMovieListType.WATCHLIST ->
                    api.getWatchlistMovies(accountId = accountId, sessionId = sessionId, page = page)
            }
            LoadResult.Page(
                data = response.results.map { it.toMovie() },
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

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
