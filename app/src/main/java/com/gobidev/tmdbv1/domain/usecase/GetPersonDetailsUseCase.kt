package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetPersonDetailsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(personId: Int): Result<PersonDetails> =
        repository.getPersonDetails(personId)
}
