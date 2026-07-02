package com.gobidev.tmdbv1

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
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

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
