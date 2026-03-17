package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetPersonCreditsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(personId: Int): Result<List<PersonCastCredit>> =
        repository.getPersonCredits(personId)
}
