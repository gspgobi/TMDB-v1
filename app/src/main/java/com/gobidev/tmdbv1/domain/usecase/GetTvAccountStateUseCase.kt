package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.AccountState
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetTvAccountStateUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(tvId: Int): Result<AccountState> =
        repository.getTvAccountState(tvId)
}
