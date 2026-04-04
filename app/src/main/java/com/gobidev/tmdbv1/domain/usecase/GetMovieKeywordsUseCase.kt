package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Keyword
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetMovieKeywordsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Keyword>> =
        repository.getMovieKeywords(movieId)
}
