package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.MovieCollectionDetails
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetCollectionDetailsUseCase @Inject constructor(
    private val repo: MovieRepository
) {
    suspend operator fun invoke(collectionId: Int): Result<MovieCollectionDetails> =
        repo.getCollectionDetails(collectionId)
}
