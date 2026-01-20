package ru.sirius.grable

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.sirius.grable.worker.ReviewNotificationWorker
import java.util.concurrent.TimeUnit

class GrableApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleReviewNotifications()
    }

    private fun scheduleReviewNotifications() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val remindersEnabled = sharedPreferences.getBoolean("toggleDailyReminders", true)

        if (!remindersEnabled) {
            WorkManager.getInstance(this).cancelUniqueWork(ReviewNotificationWorker.WORK_NAME)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val reviewWorkRequest = PeriodicWorkRequestBuilder<ReviewNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            ReviewNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reviewWorkRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val timeString = sharedPreferences.getString("reminderTime", "20:00") ?: "20:00"
        val (hour, minute) = timeString.split(":").map { it.toInt() }

        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}

