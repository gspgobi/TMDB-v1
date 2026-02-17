package com.gobidev.tmdbv1.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that adds the Bearer token authentication header
 * to all outgoing requests.
 *
 * This ensures we don't have to manually add the Authorization header
 * to every API call.
 *
 * @param apiToken The Bearer token from BuildConfig
 */
class AuthInterceptor(private val apiToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Add Authorization header with Bearer token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiToken")
            .header("Accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}
