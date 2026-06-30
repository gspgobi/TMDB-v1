package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class SetFavoriteUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(movieId: Int, favorite: Boolean): Result<Unit> =
        repository.setFavorite(movieId, favorite)
}
