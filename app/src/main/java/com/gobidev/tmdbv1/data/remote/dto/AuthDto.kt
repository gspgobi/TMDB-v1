package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestTokenResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("request_token") val requestToken: String
)

data class LoginRequestBody(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("request_token") val requestToken: String
)

data class SessionRequestBody(
    @SerializedName("request_token") val requestToken: String
)

data class SessionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("session_id") val sessionId: String
)

data class DeleteSessionBody(
    @SerializedName("session_id") val sessionId: String
)
