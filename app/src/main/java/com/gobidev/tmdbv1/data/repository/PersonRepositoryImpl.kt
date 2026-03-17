package com.gobidev.tmdbv1.data.repository

import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.remote.mapper.toPersonCastCredit
import com.gobidev.tmdbv1.data.remote.mapper.toPersonDetails
import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.domain.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonRepositoryImpl @Inject constructor(
    private val api: TMDBApiService
) : PersonRepository {

    override suspend fun getPersonDetails(personId: Int): Result<PersonDetails> = safeCall {
        api.getPersonDetails(personId).toPersonDetails()
    }

    override suspend fun getPersonCredits(personId: Int): Result<List<PersonCastCredit>> = safeCall {
        api.getPersonCombinedCredits(personId).cast
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }
            .sortedByDescending { it.popularity ?: 0.0 }
            .map { it.toPersonCastCredit() }
    }
}
