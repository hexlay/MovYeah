package hexlay.movyeah.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import hexlay.movyeah.activities.base.AbsCoreActivity
import hexlay.movyeah.api.network.view_models.WatchViewModel
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.events.StartWatchingEvent
import org.greenrobot.eventbus.EventBus

class BrowserActivity : AbsCoreActivity() {

    private var movieId = 0
    private val watchViewModel by viewModels<WatchViewModel>()

    override var useEventBus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initMovieData()
    }

    private fun initData() {
        if (Intent.ACTION_VIEW == intent.action) {
            val path = intent.data?.path
            if (path != null && path.contains("movies")) {
                movieId = path.split("/")[2].toInt()
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    private fun initMovieData() {
        watchViewModel.fetchSingleMovie(movieId).observeOnce(this, {
            EventBus.getDefault().post(StartWatchingEvent(it))
            finish()
        })
    }

}
