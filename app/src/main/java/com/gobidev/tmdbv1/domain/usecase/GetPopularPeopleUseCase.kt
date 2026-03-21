package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Person
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.util.Result
import javax.inject.Inject

class GetPopularPeopleUseCase @Inject constructor(
    private val repo: PersonRepository
) {
    suspend operator fun invoke(): Result<List<Person>> = repo.getPopularPeople()
}
