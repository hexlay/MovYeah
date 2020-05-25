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
import android.util.SparseArray
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.util.isNotEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import hexlay.movyeah.R
import hexlay.movyeah.activities.MainActivity
import hexlay.movyeah.api.database.view_models.DbMovieViewModel
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.network.view_models.WatchViewModel
import hexlay.movyeah.helpers.PreferenceHelper
import org.jetbrains.anko.intentFor

class NotificationServiceJob : JobService() {

    private var dbMovies: DbMovieViewModel? = null
    private var watchViewModel: WatchViewModel? = null

    private var fetchTvShowEpisodes: MutableLiveData<SparseArray<List<Episode>>>? = null
    private var fetchTvShowEpisodesObserver: Observer<SparseArray<List<Episode>>>? = null
    private var getMovies: LiveData<List<Movie>>? = null
    private var getMoviesObserver: Observer<List<Movie>>? = null

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (watchViewModel == null) {
            watchViewModel = WatchViewModel(application)
        }
        if (dbMovies == null) {
            dbMovies = DbMovieViewModel(application)
        }
        sync()
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        removeObserversIfAny()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserversIfAny()
    }

    private fun removeObserversIfAny() {
        getMoviesObserver?.let { getMovies?.removeObserver(it) }
        fetchTvShowEpisodesObserver?.let { fetchTvShowEpisodes?.removeObserver(it) }
    }

    private fun sync() {
        getMoviesObserver = Observer<List<Movie>> { list ->
            handleData(list)
            getMoviesObserver?.let { getMovies?.removeObserver(it) }
        }
        getMovies = dbMovies?.getMovies()
        getMovies?.observeForever(getMoviesObserver!!)
    }

    private fun handleData(data: List<Movie>) {
        if (data.isNotEmpty()) {
            data.forEach { movie ->
                if (movie.isTvShow && movie.seasons != null) {
                    fetchTvShowEpisodesObserver = Observer<SparseArray<List<Episode>>> { seasons ->
                        if (seasons != null && seasons.isNotEmpty()) {
                            val size = seasons.size()
                            val last = seasons[size].last()
                            val mockId = "${movie.adjaraId}_${last.getMockEpisodeId(size)}"
                            if (!PreferenceHelper.savedNotificationIds.contains(mockId)) {
                                createNotificationChannel("Favorites", "Favorite tv shows")
                                showNotification(movie, "S${size}E${last.episode} - ${last.getEpisodeTitle()}", size + last.episode)
                            }
                            PreferenceHelper.addNotificationHistory(mockId)
                            fetchTvShowEpisodesObserver?.let { fetchTvShowEpisodes?.removeObserver(it) }
                        }
                    }
                    fetchTvShowEpisodes = watchViewModel?.fetchTvShowEpisodes(movie.id, movie.seasons!!.data.size)
                    fetchTvShowEpisodes?.observeForever(fetchTvShowEpisodesObserver!!)
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
                        NotificationManagerCompat.from(this@NotificationServiceJob).notify(notificationId, notificationBuilder.build())
                    }
                })
    }



}
