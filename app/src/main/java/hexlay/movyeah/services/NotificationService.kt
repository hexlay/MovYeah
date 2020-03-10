package hexlay.movyeah.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.R
import hexlay.movyeah.activities.MainActivity
import hexlay.movyeah.helpers.PreferenceHelper
import hexlay.movyeah.models.movie.Movie
import org.jetbrains.anko.intentFor

// TODO:
class NotificationService : JobService() {

    private var lastId = "0"
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        preferenceHelper = PreferenceHelper(this)
        lastId = preferenceHelper.lastNotificationId
        sync()
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    private fun sync() {
        lastId = preferenceHelper.lastNotificationId
    }

    private fun createNotificationChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Movyeah", name, importance)
            channel.description = description
            val notificationManager = getSystemService<NotificationManager>(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(movie: Movie, notificationId: Int) {
        val intent = intentFor<MainActivity>("movie" to movie)
        val pendingIntent = PendingIntent.getActivity(this, notificationId, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(this, "Movyeah")
                .setContentTitle("სიახლე !")
                .setContentText(movie.primaryName)
                .setSmallIcon(R.drawable.ic_noti)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.BLUE, 400, 300)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(movie.plots?.data?.get(0)?.description))
                .setContentIntent(pendingIntent)
        Glide.with(applicationContext)
                .asBitmap()
                .load(movie.getTruePoster())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        notificationBuilder.setLargeIcon(resource)
                        NotificationManagerCompat.from(this@NotificationService).notify(notificationId, notificationBuilder.build())
                    }
                })
    }

}
