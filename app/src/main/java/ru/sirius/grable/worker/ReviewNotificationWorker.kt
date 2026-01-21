package ru.sirius.grable.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.sirius.grable.MainActivity
import ru.sirius.grable.R
import ru.sirius.grable.core.design.R as DesignR
import ru.sirius.grable.core.database.PlaylistDao
import ru.sirius.grable.core.database.WordDao

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val wordDao: WordDao by inject()
    private val playlistDao: PlaylistDao by inject()

    override suspend fun doWork(): Result {
        return try {
            val dueCount = getDueWordsCount()
            if (dueCount > 0) {
                sendNotification(dueCount)
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private suspend fun getDueWordsCount(): Int {
        val allPlaylists = playlistDao.getAllPlaylistIds()
        return wordDao.getDueWords(allPlaylists, System.currentTimeMillis()).size
    }

    private fun sendNotification(count: Int) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_OPEN_LEARN_SCREEN, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = applicationContext.getString(R.string.notification_review_title)
        val text = applicationContext.resources.getQuantityString(
            R.plurals.notification_review_text,
            count,
            count
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(applicationContext.getColor(DesignR.color.primary))
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(NOTIFICATION_ID, notification)
            } catch (e: SecurityException) {
                android.util.Log.e("ReviewNotificationWorker", "Failed to send notification", e)
            }
        }
    }

    private fun createNotificationChannel() {
        val name = applicationContext.getString(R.string.notification_channel_name)
        val descriptionText = applicationContext.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "review_notifications"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "ReviewNotificationWork"
        const val EXTRA_OPEN_LEARN_SCREEN = "open_learn_screen"
    }
}

