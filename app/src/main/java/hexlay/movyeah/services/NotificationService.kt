package hexlay.movyeah.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.util.isNotEmpty
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.R
import hexlay.movyeah.activities.MainActivity
import hexlay.movyeah.api.view_models.WatchViewModel
import hexlay.movyeah.database.view_models.DbMovieViewModel
import hexlay.movyeah.helpers.PreferenceHelper
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.movie.Movie
import org.jetbrains.anko.intentFor

class NotificationService : LifecycleService() {

    private var dbMovies: DbMovieViewModel? = null
    private var watchViewModel: WatchViewModel? = null
    private var preferenceHelper: PreferenceHelper? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (watchViewModel == null) {
            watchViewModel = WatchViewModel(application)
        }
        if (preferenceHelper == null) {
            preferenceHelper = PreferenceHelper(baseContext)
        }
        if (dbMovies == null) {
            dbMovies = DbMovieViewModel(application)
        }
        sync()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sync() {
        dbMovies?.getMovies()?.observeOnce(this, Observer {
            handleData(it)
        })
    }

    private fun handleData(data: List<Movie>) {
        if (data.isNotEmpty()) {
            data.forEach {
                if (it.isTvShow && it.seasons != null) {
                    watchViewModel?.fetchTvShowEpisodes(it.id, it.seasons!!.data.size)?.observeOnce(this, Observer { seasons ->
                        if (seasons != null && seasons.isNotEmpty()) {
                            val size = seasons.size()
                            val last = seasons[size].last()
                            val mockId = last.getMockEpisodeId(size)
                            if (mockId != preferenceHelper!!.lastNotificationId) {
                                createNotificationChannel("Favorites", "Favorite tv shows")
                                showNotification(it, "S${size}E${last.episode} - ${last.getEpisodeTitle()}", size + last.episode)
                            }
                            preferenceHelper!!.lastNotificationId = mockId
                        }
                    })
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun createNotificationChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Movyeah", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(movie: Movie, desc: String, notificationId: Int) {
        val intent = intentFor<MainActivity>("movie" to movie)
        val pendingIntent = PendingIntent.getActivity(this, notificationId, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(this, "Movyeah")
                .setContentTitle("${movie.getTitle()} - ახალი ეპიზოდი !")
                .setContentText(movie.getTitle())
                .setSmallIcon(R.drawable.ic_noti)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.BLUE, 400, 300)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(desc))
                .setContentIntent(pendingIntent)
        Glide.with(applicationContext)
                .asBitmap()
                .load(movie.getTruePoster())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        notificationBuilder.setLargeIcon(resource)
                        NotificationManagerCompat.from(this@NotificationService).notify(notificationId, notificationBuilder.build())
                    }
                })
    }

}