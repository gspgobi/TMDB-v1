package com.gobidev.tmdbv1.domain.model

data class ExternalIds(
    val imdbId: String?,
    val wikidataId: String?,
    val facebookId: String?,
    val instagramId: String?,
    val twitterId: String?,
    val tvdbId: String? = null
) {
    fun hasAny(): Boolean =
        !imdbId.isNullOrBlank() || !facebookId.isNullOrBlank() ||
        !instagramId.isNullOrBlank() || !twitterId.isNullOrBlank() ||
        !wikidataId.isNullOrBlank() || !tvdbId.isNullOrBlank()
}
