package hexlay.movyeah.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import hexlay.movyeah.helpers.startBackgroundService
import org.jetbrains.anko.intentFor

class NotificationServiceJob : JobService() {

    private var service: Intent? = null

    override fun onCreate() {
        super.onCreate()
        service = intentFor<NotificationService>()
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        startBackgroundService(service)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        stopService(service)
        return true
    }


}
