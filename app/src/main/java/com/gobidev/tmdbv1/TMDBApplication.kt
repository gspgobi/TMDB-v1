package com.gobidev.tmdbv1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for TMDB app.
 *
 * @HiltAndroidApp triggers Hilt's code generation including a base class for the application
 * that serves as the application-level dependency container.
 */
@HiltAndroidApp
class TMDBApplication : Application() {

}
