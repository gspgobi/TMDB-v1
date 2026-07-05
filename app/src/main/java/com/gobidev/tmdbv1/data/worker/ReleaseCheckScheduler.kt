package com.gobidev.tmdbv1.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps [WorkManager] enqueue/cancel calls for [ReleaseCheckWorker] so the
 * periodic and on-demand request construction isn't duplicated across callers.
 */
@Singleton
class ReleaseCheckScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager get() = WorkManager.getInstance(context)

    fun enqueuePeriodic() {
        val request = PeriodicWorkRequestBuilder<ReleaseCheckWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            ReleaseCheckWorker.PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelPeriodic() {
        workManager.cancelUniqueWork(ReleaseCheckWorker.PERIODIC_WORK_NAME)
    }

    fun enqueueOneTime(): Operation {
        val request = OneTimeWorkRequestBuilder<ReleaseCheckWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        return workManager.enqueueUniqueWork(
            ReleaseCheckWorker.ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
