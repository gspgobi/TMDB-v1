package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.repository.AuthRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.logout()
}
