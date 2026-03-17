package com.gobidev.tmdbv1.domain.model

data class PersonDetails(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    val placeOfBirth: String?,
    val profileUrl: String?,
    val knownForDepartment: String,
    val popularity: Double,
    val gender: Int
)

data class PersonCastCredit(
    val id: Int,
    val title: String,
    val character: String,
    val mediaType: String,
    val posterUrl: String?,
    val releaseDate: String?
)
