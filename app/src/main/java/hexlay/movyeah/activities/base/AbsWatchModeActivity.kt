package hexlay.movyeah.activities.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import hexlay.movyeah.R
import hexlay.movyeah.fragments.WatchFragment
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.PreferenceHelper
import hexlay.movyeah.models.events.StartWatchingEvent
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.services.ConnectivityReceiver
import kotlinx.android.synthetic.main.fragment_watch.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class AbsWatchModeActivity : AppCompatActivity() {

    private var pipActions: BroadcastReceiver? = null
    protected var preferenceHelper: PreferenceHelper? = null
    protected var connectivityReceiver: ConnectivityReceiver? = null

    protected var watchMode = false
    protected var registered = false
    protected var watchFragment: WatchFragment? = null

    protected open fun initActivity() {
        preferenceHelper = PreferenceHelper(this)
        connectivityReceiver = ConnectivityReceiver()
    }

    protected fun registerConReceiver() {
        if (!registered) {
            try {
                val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
                filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
                filter.addAction("android.net.wifi.STATE_CHANGE")
                registerReceiver(connectivityReceiver, filter)
                registered = true
            } catch (e: Exception) {
                Log.e("registerConReceiver", e.message!!)
            }
        }
    }

    protected fun unregisterConReceiver() {
        if (registered) {
            try {
                unregisterReceiver(connectivityReceiver)
                registered = false
            } catch (e: Exception) {
                Log.e("unregisterConReceiver", e.message!!)
            }
        }
    }

    @Subscribe
    fun listenWatch(event: StartWatchingEvent) {
        startWatchMode(event.item)
    }

    protected open fun startWatchMode(movie: Movie) {
        if (!isInWatchMode()) {
            watchMode = true
            watchFragment = WatchFragment.newInstance(movie)
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                add(android.R.id.content, watchFragment!!, "watch_mode")
            }
        }
    }

    protected open fun endWatchMode() {
        if (isInWatchMode()) {
            watchMode = false
            watchFragment!!.superExit = true
            supportFragmentManager.commit {
                setCustomAnimations(R.anim.anim_enter, R.anim.anim_exit)
                remove(watchFragment!!)
            }
            watchFragment = null
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInWatchMode()) {
            watchFragment!!.isInPIP = isInPictureInPictureMode
            if (isInPictureInPictureMode) {
                pipActions = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent?) {
                        if (intent != null && intent.action != null && intent.action == "media_control") {
                            when (intent.getIntExtra("control_type", 0)) {
                                Constants.CONTROL_TYPE_PLAY -> {
                                    watchFragment!!.pipStartPlayer()
                                }
                                Constants.CONTROL_TYPE_PAUSE -> {
                                    watchFragment!!.pipStopPlayer()
                                }
                            }
                        }
                    }
                }
                registerReceiver(pipActions, IntentFilter("media_control"))
            } else {
                toolbar.visibility = View.VISIBLE
                watchFragment!!.pipRevert()
                unregisterReceiver(pipActions)
            }
        }
    }

    protected fun isInWatchMode(): Boolean {
        return watchMode && watchFragment != null
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        unregisterConReceiver()
        super.onStop()
    }

}