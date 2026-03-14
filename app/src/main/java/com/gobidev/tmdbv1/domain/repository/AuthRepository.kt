package com.gobidev.tmdbv1.domain.repository

import com.gobidev.tmdbv1.domain.util.Result

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}
