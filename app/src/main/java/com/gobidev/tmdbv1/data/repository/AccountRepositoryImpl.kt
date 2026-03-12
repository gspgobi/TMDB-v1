package com.gobidev.tmdbv1.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.data.paging.AccountMovieListType
import com.gobidev.tmdbv1.data.paging.AccountMoviesPagingSource
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toUserAccount
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.UserAccount
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val api: TMDBApiService,
    private val sessionManager: SessionManager
) : AccountRepository {

    override suspend fun getAccount(): Result<UserAccount> = safeCall {
        val sessionId = sessionManager.sessionId ?: error("Not logged in")
        api.getAccount(sessionId = sessionId).toUserAccount()
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
}
