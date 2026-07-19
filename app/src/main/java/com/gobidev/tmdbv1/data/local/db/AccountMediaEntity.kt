package com.gobidev.tmdbv1.data.local.db

import androidx.room.Entity
import androidx.room.Index

enum class MediaType { MOVIE, TV }
enum class AccountListType { FAVORITES, WATCHLIST }

/**
 * Local cache of a page of TMDB's account favorite/watchlist feeds.
 * [position] preserves server order since TMDB's paged lists have no sortable column.
 */
@Entity(
    tableName = "account_media",
    primaryKeys = ["accountId", "mediaType", "listType", "mediaId"],
    indices = [Index(value = ["accountId", "mediaType", "listType", "position"])]
)
data class AccountMediaEntity(
    val accountId: Int,
    val mediaType: String,
    val listType: String,
    val mediaId: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val dateLabel: String,
    val rating: Double,
    val voteCount: Int,
    val position: Int,
    val fetchedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "account_media_remote_keys",
    primaryKeys = ["accountId", "mediaType", "listType"]
)
data class AccountMediaRemoteKeyEntity(
    val accountId: Int,
    val mediaType: String,
    val listType: String,
    val nextPage: Int?,
    val currentPage: Int
)
