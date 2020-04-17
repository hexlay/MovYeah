package movyeahtv.activities

import androidx.fragment.app.commit
import hexlay.movyeah.models.movie.Movie
import movyeahtv.activities.base.TvBaseFragmentActivity
import movyeahtv.fragments.TvWatchFragment
import movyeahtv.models.events.watch.MovieErrorEvent
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