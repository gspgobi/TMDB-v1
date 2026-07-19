package com.gobidev.tmdbv1.data.local.mapper

import com.gobidev.tmdbv1.data.local.db.AccountMediaEntity
import com.gobidev.tmdbv1.domain.model.TvShow

fun AccountMediaEntity.toTvShow(): TvShow = TvShow(
    id = mediaId,
    name = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    firstAirDate = dateLabel,
    rating = rating,
    voteCount = voteCount
)
