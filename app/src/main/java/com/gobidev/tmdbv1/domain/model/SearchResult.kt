package com.gobidev.tmdbv1.domain.model

sealed class SearchResult {
    data class MovieResult(val movie: Movie) : SearchResult()
    data class TvResult(val show: TvShow) : SearchResult()
    data class PersonResult(val person: Person) : SearchResult()
}
