package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.AccountResponse
import com.gobidev.tmdbv1.domain.model.UserAccount

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val PROFILE_SIZE = "w185"

fun AccountResponse.toUserAccount(): UserAccount {
    val avatarUrl = avatar.tmdb?.avatarPath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" }
        ?: avatar.gravatar?.hash?.let { "https://secure.gravatar.com/avatar/$it" }
    return UserAccount(
        id = id,
        username = username,
        name = name,
        avatarUrl = avatarUrl
    )
}
