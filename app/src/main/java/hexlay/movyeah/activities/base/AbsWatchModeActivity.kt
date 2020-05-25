package hexlay.movyeah.activities.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.rezwan.knetworklib.KNetwork
import hexlay.movyeah.R
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.fragments.WatchFragment
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.models.events.NetworkChangeEvent
import hexlay.movyeah.models.events.StartWatchingEvent
import kotlinx.android.synthetic.main.fragment_watch.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class AbsWatchModeActivity : AppCompatActivity() {

    private var pipActions: BroadcastReceiver? = null

    protected var watchMode = false
    protected var watchFragment: WatchFragment? = null

    protected abstract var networkView: Int

    protected open fun initActivity() {
        KNetwork.bind(this, lifecycle)
                .setInAnimation(R.anim.top_in)
                .setOutAnimation(R.anim.top_out)
                .setViewGroupResId(networkView)
                .setConnectivityListener(object : KNetwork.OnNetWorkConnectivityListener {
            override fun onNetConnected() {
                EventBus.getDefault().post(NetworkChangeEvent(true))
            }

            override fun onNetDisConnected() {
                EventBus.getDefault().post(NetworkChangeEvent(false))
            }
        })
    }

    protected open fun startWatchMode(movie: Movie, identifier: String = "") {
        if (!isInWatchMode()) {
            watchMode = true
            watchFragment = WatchFragment.newInstance(movie, identifier)
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
        super.onStop()
    }

    @Subscribe
    fun listenWatch(event: StartWatchingEvent) {
        startWatchMode(event.item, event.identifier)
    }

}