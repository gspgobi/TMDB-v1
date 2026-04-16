package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.ExternalIds
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetPersonExternalIdsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(personId: Int): Result<ExternalIds> =
        repository.getPersonExternalIds(personId)
}
