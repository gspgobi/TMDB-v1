package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: AccountAvatarDto
)

data class AccountAvatarDto(
    @SerializedName("tmdb") val tmdb: TmdbAvatarDto?,
    @SerializedName("gravatar") val gravatar: GravatarDto?
)

data class TmdbAvatarDto(
    @SerializedName("avatar_path") val avatarPath: String?
)

data class GravatarDto(
    @SerializedName("hash") val hash: String?
)
