package com.gobidev.tmdbv1.domain.repository

import androidx.paging.PagingData
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.UserAccount
import com.gobidev.tmdbv1.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAccount(): Result<UserAccount>
    fun getFavoriteMovies(): Flow<PagingData<Movie>>
    fun getWatchlistMovies(): Flow<PagingData<Movie>>
}
