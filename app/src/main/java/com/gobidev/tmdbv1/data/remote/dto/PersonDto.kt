package com.gobidev.tmdbv1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PopularPersonListResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<PopularPersonDto>
)

data class PopularPersonDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("known_for_department") val knownForDepartment: String?,
    @SerializedName("popularity") val popularity: Double
)

data class PersonDetailsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("biography") val biography: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("deathday") val deathday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("known_for_department") val knownForDepartment: String?,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("gender") val gender: Int
)

data class PersonCombinedCreditsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("cast") val cast: List<PersonCastCreditDto>
)

data class PersonCastCreditDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("character") val character: String?,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("popularity") val popularity: Double?
)
