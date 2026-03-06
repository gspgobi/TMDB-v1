package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetMoviePreviewUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(type: MovieListType): Result<List<Movie>> =
        repository.getMoviesPreview(type)
}
