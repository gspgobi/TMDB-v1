package com.gobidev.tmdbv1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.data.worker.ReleaseCheckScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for TMDB app.
 *
 * @HiltAndroidApp triggers Hilt's code generation including a base class for the application
 * that serves as the application-level dependency container.
 *
 * Implements [Configuration.Provider] so WorkManager uses [HiltWorkerFactory] to construct
 * `@HiltWorker`-annotated workers with their dependencies injected.
 */
@HiltAndroidApp
class TMDBApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var releaseCheckScheduler: ReleaseCheckScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createReleaseNotificationChannel()
        if (sessionManager.isLoggedIn) {
            releaseCheckScheduler.enqueuePeriodic()
        }
    }

    private fun createReleaseNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            RELEASE_CHANNEL_ID,
            "Watchlist releases",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies you when a watchlisted movie is released"
        }
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }

    companion object {
        const val RELEASE_CHANNEL_ID = "watchlist_releases"
    }
}
