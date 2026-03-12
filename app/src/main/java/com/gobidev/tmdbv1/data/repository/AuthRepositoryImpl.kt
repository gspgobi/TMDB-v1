package com.gobidev.tmdbv1.data.repository

import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.dto.DeleteSessionBody
import com.gobidev.tmdbv1.data.remote.dto.LoginRequestBody
import com.gobidev.tmdbv1.data.remote.dto.SessionRequestBody
import com.gobidev.tmdbv1.domain.repository.AuthRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: TMDBApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> = safeCall {
        // Step 1: Request token
        val tokenResponse = api.createRequestToken()
        val requestToken = tokenResponse.requestToken

        // Step 2: Validate with credentials
        api.validateWithLogin(
            LoginRequestBody(
                username = username,
                password = password,
                requestToken = requestToken
            )
        )

        // Step 3: Create session
        val sessionResponse = api.createSession(SessionRequestBody(requestToken = requestToken))
        sessionManager.sessionId = sessionResponse.sessionId

        // Step 4: Fetch and store account ID
        val account = api.getAccount(sessionId = sessionResponse.sessionId)
        sessionManager.accountId = account.id
    }

    override suspend fun logout(): Result<Unit> = safeCall {
        val sessionId = sessionManager.sessionId ?: return@safeCall
        api.deleteSession(DeleteSessionBody(sessionId = sessionId))
        sessionManager.clearSession()
    }
}
