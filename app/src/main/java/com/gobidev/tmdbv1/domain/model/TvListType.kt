package com.gobidev.tmdbv1.domain.model

enum class TvListType(val title: String, val routeKey: String) {
    POPULAR("Popular Series", "tv_popular"),
    TOP_RATED("Top Rated Series", "tv_top_rated"),
    ON_THE_AIR("On The Air", "tv_on_the_air"),
    AIRING_TODAY("Airing Today", "tv_airing_today");

    fun defaultSortApiValue(): String = when (this) {
        TOP_RATED -> "vote_average.desc"
        else -> "popularity.desc"
    }

    companion object {
        fun fromRouteKey(key: String): TvListType =
            entries.find { it.routeKey == key } ?: POPULAR
    }
}
