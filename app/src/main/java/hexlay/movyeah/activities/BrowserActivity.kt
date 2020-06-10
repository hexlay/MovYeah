package hexlay.movyeah.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import hexlay.movyeah.R
import hexlay.movyeah.activities.base.AbsWatchModeActivity
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.WatchViewModel
import hexlay.movyeah.fragments.WatchFragment
import hexlay.movyeah.helpers.makeFullscreen
import hexlay.movyeah.helpers.observeOnce

class BrowserActivity : AbsWatchModeActivity() {

    private var movieId = 0
    private val watchViewModel by viewModels<WatchViewModel>()

    override var networkView: Int = android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        makeFullscreen()
        initMovieData()
    }

    private fun initData() {
        if (Intent.ACTION_VIEW == intent.action) {
            val path = intent.data?.path
            if (path != null && path.contains("movies")) {
                movieId = path.split("/")[2].toInt()
            } else {
                onBackPressed()
            }
        }
    }

    private fun initMovieData() {
        watchViewModel.fetchSingleMovie(movieId).observeOnce(this, Observer {
            startWatchMode(it)
        })
    }

    override fun startWatchMode(movie: Movie, identifier: String) {
        if (!isInWatchMode()) {
            watchMode = true
            watchFragment = WatchFragment.newInstance(movie)
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                add(android.R.id.content, watchFragment!!, "watch_mode")
            }
        }
    }

    override fun endWatchMode() {
        if (isInWatchMode()) {
            watchMode = false
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                remove(watchFragment!!)
            }
            watchFragment = null
        }
    }

    override fun onBackPressed() {
        endWatchMode()
        finish()
    }

}
