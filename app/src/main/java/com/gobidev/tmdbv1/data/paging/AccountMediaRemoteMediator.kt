package com.gobidev.tmdbv1.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.gobidev.tmdbv1.data.local.db.AccountListType
import com.gobidev.tmdbv1.data.local.db.AccountMediaDao
import com.gobidev.tmdbv1.data.local.db.AccountMediaEntity
import com.gobidev.tmdbv1.data.local.db.AccountMediaRemoteKeyEntity
import com.gobidev.tmdbv1.data.local.db.MediaType
import com.gobidev.tmdbv1.data.local.db.TMDBDatabase
import retrofit2.HttpException
import java.io.IOException

/**
 * Syncs one (accountId, mediaType, listType) TMDB feed into [AccountMediaDao].
 * Room is the source of truth the UI pages from; this only refills it from the network.
 * REFRESH replaces the cached page 1; APPEND fetches the next page from the stored remote key.
 */
@OptIn(ExperimentalPagingApi::class)
class AccountMediaRemoteMediator(
    private val accountId: Int,
    private val mediaType: MediaType,
    private val listType: AccountListType,
    private val db: TMDBDatabase,
    private val dao: AccountMediaDao = db.accountMediaDao(),
    private val fetchPage: suspend (page: Int) -> Pair<List<AccountMediaEntity>, Int>
) : RemoteMediator<Int, AccountMediaEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AccountMediaEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val key = dao.remoteKey(accountId, mediaType.name, listType.name)
                    key?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = key != null)
                }
            }

            val (entities, totalPages) = fetchPage(page)
            val endReached = page >= totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dao.clear(accountId, mediaType.name, listType.name)
                    dao.clearRemoteKey(accountId, mediaType.name, listType.name)
                }
                dao.upsertAll(entities)
                dao.upsertRemoteKey(
                    AccountMediaRemoteKeyEntity(
                        accountId = accountId,
                        mediaType = mediaType.name,
                        listType = listType.name,
                        nextPage = if (endReached) null else page + 1,
                        currentPage = page
                    )
                )
            }
            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
