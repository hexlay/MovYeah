package hexlay.movyeah.app_tv.activities

import androidx.fragment.app.commit
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.app_tv.activities.base.TvBaseFragmentActivity
import hexlay.movyeah.app_tv.fragments.TvWatchFragment
import hexlay.movyeah.app_tv.models.events.watch.MovieErrorEvent
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast

class TvWatchActivity : TvBaseFragmentActivity() {

    override fun initMainFragment() {
        val movie = intent.getParcelableExtra<Movie>("movie")
        if (movie != null) {
            val watchFragment = TvWatchFragment.newInstance(movie)
            supportFragmentManager.commit {
                replace(android.R.id.content, watchFragment, "watch")
            }
        } else {
            finish()
        }
    }

    @Subscribe
    fun listenMovieError(event: MovieErrorEvent) {
        toast(event.resId)
        finish()
    }

}