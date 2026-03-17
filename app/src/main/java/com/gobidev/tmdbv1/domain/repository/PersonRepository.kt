package com.gobidev.tmdbv1.domain.repository

import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.util.Result

interface PersonRepository {
    suspend fun getPersonDetails(personId: Int): Result<PersonDetails>
    suspend fun getPersonCredits(personId: Int): Result<List<PersonCastCredit>>
}
