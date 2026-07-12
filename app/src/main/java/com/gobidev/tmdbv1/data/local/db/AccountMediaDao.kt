package com.gobidev.tmdbv1.data.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AccountMediaDao {

    @Query(
        "SELECT * FROM account_media " +
            "WHERE accountId = :accountId AND mediaType = :mediaType AND listType = :listType " +
            "ORDER BY position ASC"
    )
    fun pagingSource(accountId: Int, mediaType: String, listType: String): PagingSource<Int, AccountMediaEntity>

    @Upsert
    suspend fun upsertAll(items: List<AccountMediaEntity>)

    @Query("DELETE FROM account_media WHERE accountId = :accountId AND mediaType = :mediaType AND listType = :listType")
    suspend fun clear(accountId: Int, mediaType: String, listType: String)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM account_media " +
            "WHERE accountId = :accountId AND mediaType = :mediaType AND listType = :listType AND mediaId = :mediaId)"
    )
    suspend fun exists(accountId: Int, mediaType: String, listType: String, mediaId: Int): Boolean

    @Query("SELECT * FROM account_media_remote_keys WHERE accountId = :accountId AND mediaType = :mediaType AND listType = :listType")
    suspend fun remoteKey(accountId: Int, mediaType: String, listType: String): AccountMediaRemoteKeyEntity?

    @Upsert
    suspend fun upsertRemoteKey(key: AccountMediaRemoteKeyEntity)

    @Query("DELETE FROM account_media_remote_keys WHERE accountId = :accountId AND mediaType = :mediaType AND listType = :listType")
    suspend fun clearRemoteKey(accountId: Int, mediaType: String, listType: String)

    @Query("DELETE FROM account_media WHERE accountId = :accountId")
    suspend fun clearAllForAccount(accountId: Int)

    @Query("DELETE FROM account_media_remote_keys WHERE accountId = :accountId")
    suspend fun clearAllRemoteKeysForAccount(accountId: Int)
}
