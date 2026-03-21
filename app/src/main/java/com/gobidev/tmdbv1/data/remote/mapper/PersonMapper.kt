package com.gobidev.tmdbv1.data.remote.mapper

import com.gobidev.tmdbv1.data.remote.dto.PersonCastCreditDto
import com.gobidev.tmdbv1.data.remote.dto.PersonDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.PopularPersonDto
import com.gobidev.tmdbv1.domain.model.Person
import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.util.toFormattedDate

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val PROFILE_SIZE = "w185"
private const val POSTER_SIZE = "w500"

fun PersonDetailsResponse.toPersonDetails(): PersonDetails = PersonDetails(
    id = id,
    name = name,
    biography = biography ?: "",
    birthday = birthday?.toFormattedDate(),
    deathday = deathday?.toFormattedDate(),
    placeOfBirth = placeOfBirth,
    profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" },
    knownForDepartment = knownForDepartment ?: "Acting",
    popularity = popularity,
    gender = gender
)

fun PopularPersonDto.toPerson(): Person = Person(
    id = id,
    name = name,
    profileUrl = profilePath?.let { "$IMAGE_BASE_URL$PROFILE_SIZE$it" },
    knownForDepartment = knownForDepartment ?: "Acting"
)

fun PersonCastCreditDto.toPersonCastCredit(): PersonCastCredit = PersonCastCredit(
    id = id,
    title = title ?: name ?: "Unknown",
    character = character ?: "",
    mediaType = mediaType,
    posterUrl = posterPath?.let { "$IMAGE_BASE_URL$POSTER_SIZE$it" },
    releaseDate = (releaseDate ?: firstAirDate)?.toFormattedDate()
)
