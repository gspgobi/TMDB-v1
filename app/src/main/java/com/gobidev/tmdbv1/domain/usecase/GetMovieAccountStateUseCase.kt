package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.AccountState
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetMovieAccountStateUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(movieId: Int): Result<AccountState> =
        repository.getMovieAccountState(movieId)
}
