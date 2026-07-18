package com.gobidev.tmdbv1.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.data.local.db.AccountListType
import com.gobidev.tmdbv1.data.local.db.AccountMediaDao
import com.gobidev.tmdbv1.data.local.db.AccountMediaEntity
import com.gobidev.tmdbv1.data.local.db.MediaType
import com.gobidev.tmdbv1.data.local.db.TMDBDatabase
import com.gobidev.tmdbv1.data.local.mapper.toTvShow
import com.gobidev.tmdbv1.data.paging.AccountMediaRemoteMediator
import com.gobidev.tmdbv1.data.paging.AccountMovieListType
import com.gobidev.tmdbv1.data.paging.AccountMoviesPagingSource
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.dto.FavoriteRequestBody
import com.gobidev.tmdbv1.data.remote.dto.WatchlistRequestBody
import com.gobidev.tmdbv1.data.remote.mapper.toAccountMediaEntity
import com.gobidev.tmdbv1.data.remote.mapper.toMovie
import com.gobidev.tmdbv1.data.remote.mapper.toUserAccount
import com.gobidev.tmdbv1.domain.model.AccountState
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.domain.model.UserAccount
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val api: TMDBApiService,
    private val sessionManager: SessionManager,
    private val db: TMDBDatabase,
    private val accountMediaDao: AccountMediaDao
) : AccountRepository {

    override suspend fun getAccount(): Result<UserAccount> {
        val sessionId = sessionManager.sessionId ?: return Result.Error("Not logged in")
        return when (val result = safeCall { api.getAccount(sessionId = sessionId).toUserAccount() }) {
            is Result.Success -> {
                sessionManager.cachedUsername = result.data.username
                sessionManager.cachedName = result.data.name
                sessionManager.cachedAvatarUrl = result.data.avatarUrl
                result
            }
            is Result.Error -> {
                val cachedUsername = sessionManager.cachedUsername
                if (cachedUsername != null) {
                    Result.Success(
                        UserAccount(
                            id = sessionManager.accountId,
                            username = cachedUsername,
                            name = sessionManager.cachedName ?: "",
                            avatarUrl = sessionManager.cachedAvatarUrl
                        )
                    )
                } else {
                    result
                }
            }
        }
    }

    override fun getFavoriteMovies(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false),
        pagingSourceFactory = {
            AccountMoviesPagingSource(
                api = api,
                accountId = sessionManager.accountId,
                sessionId = sessionManager.sessionId ?: "",
                listType = AccountMovieListType.FAVORITES
            )
        }
    ).flow

    override fun getWatchlistMovies(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false),
        pagingSourceFactory = {
            AccountMoviesPagingSource(
                api = api,
                accountId = sessionManager.accountId,
                sessionId = sessionManager.sessionId ?: "",
                listType = AccountMovieListType.WATCHLIST
            )
        }
    ).flow

    override suspend fun getWatchlistMoviesSnapshot(maxPages: Int): Result<List<Movie>> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        val movies = mutableListOf<Movie>()
        for (page in 1..maxPages) {
            val response = api.getWatchlistMovies(
                accountId = sessionManager.accountId,
                sessionId = sessionId,
                page = page
            )
            movies += response.results.map { it.toMovie() }
            if (page >= response.totalPages) break
        }
        movies
    }

    override suspend fun setFavorite(movieId: Int, favorite: Boolean): Result<Unit> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        api.markAsFavorite(
            accountId = sessionManager.accountId,
            sessionId = sessionId,
            body = FavoriteRequestBody(mediaId = movieId, favorite = favorite)
        )
    }

    override suspend fun setWatchlist(movieId: Int, watchlist: Boolean): Result<Unit> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        api.markAsWatchlist(
            accountId = sessionManager.accountId,
            sessionId = sessionId,
            body = WatchlistRequestBody(mediaId = movieId, watchlist = watchlist)
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getFavoriteTvShows(): Flow<PagingData<TvShow>> =
        buildTvPager(AccountListType.FAVORITES).flow.map { pagingData -> pagingData.map { it.toTvShow() } }

    @OptIn(ExperimentalPagingApi::class)
    override fun getWatchlistTvShows(): Flow<PagingData<TvShow>> =
        buildTvPager(AccountListType.WATCHLIST).flow.map { pagingData -> pagingData.map { it.toTvShow() } }

    @OptIn(ExperimentalPagingApi::class)
    private fun buildTvPager(listType: AccountListType): Pager<Int, AccountMediaEntity> {
        val accountId = sessionManager.accountId
        val sessionId = sessionManager.sessionId ?: ""
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false),
            remoteMediator = AccountMediaRemoteMediator(
                accountId = accountId,
                mediaType = MediaType.TV,
                listType = listType,
                db = db,
                dao = accountMediaDao
            ) { page ->
                val response = when (listType) {
                    AccountListType.FAVORITES -> api.getFavoriteTv(accountId, sessionId, page)
                    AccountListType.WATCHLIST -> api.getWatchlistTv(accountId, sessionId, page)
                }
                val entities = response.results.mapIndexed { index, dto ->
                    dto.toAccountMediaEntity(accountId, listType, position = (page - 1) * 20 + index)
                }
                entities to response.totalPages
            },
            pagingSourceFactory = {
                accountMediaDao.pagingSource(accountId, MediaType.TV.name, listType.name)
            }
        )
    }

    override suspend fun setFavoriteTv(tvId: Int, favorite: Boolean): Result<Unit> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        api.markAsFavorite(
            accountId = sessionManager.accountId,
            sessionId = sessionId,
            body = FavoriteRequestBody(mediaType = "tv", mediaId = tvId, favorite = favorite)
        )
    }

    override suspend fun setWatchlistTv(tvId: Int, watchlist: Boolean): Result<Unit> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        api.markAsWatchlist(
            accountId = sessionManager.accountId,
            sessionId = sessionId,
            body = WatchlistRequestBody(mediaType = "tv", mediaId = tvId, watchlist = watchlist)
        )
    }

    override suspend fun getMovieAccountState(movieId: Int): Result<AccountState> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        val response = api.getMovieAccountStates(movieId, sessionId)
        AccountState(favorite = response.favorite, watchlist = response.watchlist)
    }

    override suspend fun getTvAccountState(tvId: Int): Result<AccountState> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        val response = api.getTvAccountStates(tvId, sessionId)
        AccountState(favorite = response.favorite, watchlist = response.watchlist)
    }
}
