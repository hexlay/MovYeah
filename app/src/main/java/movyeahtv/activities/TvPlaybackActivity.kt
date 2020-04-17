package movyeahtv.activities

import androidx.fragment.app.commit
import movyeahtv.activities.base.TvBaseFragmentActivity
import movyeahtv.fragments.TvPlaybackFragment
import movyeahtv.models.PlaybackModel

class TvPlaybackActivity : TvBaseFragmentActivity() {

    override fun initMainFragment() {
        val playback = intent.getParcelableExtra<PlaybackModel>("playback")
        if (playback != null) {
            val playbackFragment = TvPlaybackFragment.newInstance(playback)
            supportFragmentManager.commit {
                replace(android.R.id.content, playbackFragment, "playback")
            }
        } else {
            finish()
        }
    }

}