package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.MovieImages
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetMovieImagesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<MovieImages> =
        repository.getMovieImages(movieId)
}
