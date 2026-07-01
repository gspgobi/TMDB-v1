package com.gobidev.tmdbv1.domain.usecase

import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.util.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Returns the watchlisted movies that have already released (release date
 * on or before today), based on the un-formatted [Movie.releaseDateIso].
 */
class CheckNewlyReleasedWatchlistMoviesUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): Result<List<Movie>> {
        return when (val result = repository.getWatchlistMoviesSnapshot()) {
            is Result.Success -> {
                val today = isoDateFormat.format(Date())
                Result.Success(
                    result.data.filter { it.releaseDateIso.isNotBlank() && it.releaseDateIso <= today }
                )
            }
            is Result.Error -> result
        }
    }

    private companion object {
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}
