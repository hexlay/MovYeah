package hexlay.movyeah.app_tv.activities

import androidx.fragment.app.commit
import hexlay.movyeah.app_tv.activities.base.TvBaseFragmentActivity
import hexlay.movyeah.app_tv.fragments.TvPlaybackFragment
import hexlay.movyeah.app_tv.models.PlaybackModel

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