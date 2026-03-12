package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.UserAccount
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): Result<UserAccount> =
        repository.getAccount()
}
