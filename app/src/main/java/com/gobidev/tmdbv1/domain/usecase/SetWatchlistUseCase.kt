package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class SetWatchlistUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(movieId: Int, watchlist: Boolean): Result<Unit> =
        repository.setWatchlist(movieId, watchlist)
}
