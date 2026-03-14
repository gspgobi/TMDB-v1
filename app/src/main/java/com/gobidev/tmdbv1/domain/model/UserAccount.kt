package com.gobidev.tmdbv1.domain.model

data class UserAccount(
    val id: Int,
    val username: String,
    val name: String,
    val avatarUrl: String?
)
