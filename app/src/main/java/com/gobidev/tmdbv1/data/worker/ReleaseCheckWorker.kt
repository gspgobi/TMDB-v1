package com.gobidev.tmdbv1.data.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.gobidev.tmdbv1.R
import com.gobidev.tmdbv1.TMDBApplication
import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.data.local.db.NotifiedMovieDao
import com.gobidev.tmdbv1.data.local.db.NotifiedMovieEntity
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.usecase.CheckNewlyReleasedWatchlistMoviesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.gobidev.tmdbv1.domain.util.Result as DomainResult

/**
 * Checks the logged-in user's watchlist for movies that have released and
 * haven't been notified about yet, then posts a local notification for each.
 * Serves both the periodic and on-demand [ReleaseCheckScheduler] work requests.
 */
@HiltWorker
class ReleaseCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val checkNewlyReleasedWatchlistMoviesUseCase: CheckNewlyReleasedWatchlistMoviesUseCase,
    private val notifiedMovieDao: NotifiedMovieDao,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!sessionManager.isLoggedIn) return Result.success()

        return when (val result = checkNewlyReleasedWatchlistMoviesUseCase()) {
            is DomainResult.Error -> Result.retry()
            is DomainResult.Success -> {
                val alreadyNotified = notifiedMovieDao.getAllNotifiedIds().toSet()
                val newlyReleased = result.data.filterNot { it.id in alreadyNotified }

                newlyReleased.forEach { movie ->
                    postReleaseNotification(movie)
                    notifiedMovieDao.markNotified(NotifiedMovieEntity(movie.id, System.currentTimeMillis()))
                }

                Result.success(
                    Data.Builder().putInt(KEY_NEW_RELEASES_COUNT, newlyReleased.size).build()
                )
            }
        }
    }

    private fun postReleaseNotification(movie: Movie) {
        val hasPermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val notification = NotificationCompat.Builder(applicationContext, TMDBApplication.RELEASE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_movie)
            .setContentTitle("Now released")
            .setContentText("${movie.title} is now available")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(movie.id, notification)
    }

    companion object {
        const val KEY_NEW_RELEASES_COUNT = "new_releases_count"
        const val PERIODIC_WORK_NAME = "release_check_periodic"
        const val ONE_TIME_WORK_NAME = "release_check_one_time"
    }
}
