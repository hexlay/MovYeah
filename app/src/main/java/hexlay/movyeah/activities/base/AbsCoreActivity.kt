package hexlay.movyeah.activities.base

import androidx.appcompat.app.AppCompatActivity
import com.rezwan.knetworklib.KNetwork
import hexlay.movyeah.R
import hexlay.movyeah.activities.MovieActivity
import hexlay.movyeah.api.helpers.isNetworkAvailable
import hexlay.movyeah.helpers.initDarkMode
import hexlay.movyeah.helpers.makeFullscreen
import hexlay.movyeah.models.events.NetworkChangeEvent
import hexlay.movyeah.models.events.StartWatchingEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

abstract class AbsCoreActivity : AppCompatActivity() {

    protected open var networkView: Int = android.R.id.content
    protected open var isNetworkAvailable = true

    protected open fun initActivity() {
        initDarkMode()
        makeFullscreen()
        isNetworkAvailable = isNetworkAvailable()
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
        startActivity(intentFor<MovieActivity>("movie" to event.item).newTask())
    }

}