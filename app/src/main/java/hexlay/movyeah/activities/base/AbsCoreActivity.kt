package hexlay.movyeah.activities.base

import androidx.fragment.app.FragmentActivity
import com.rezwan.knetworklib.KNetwork
import hexlay.movyeah.R
import hexlay.movyeah.api.helpers.isNetworkAvailable
import hexlay.movyeah.helpers.initDarkMode
import hexlay.movyeah.helpers.makeFullscreen
import hexlay.movyeah.models.events.NetworkChangeEvent
import org.greenrobot.eventbus.EventBus

abstract class AbsCoreActivity : FragmentActivity() {

    private var networkView = android.R.id.content

    protected var isNetworkAvailable = true
    protected open var useEventBus = true

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
        if (useEventBus) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this) && useEventBus) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    public override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
    }

}