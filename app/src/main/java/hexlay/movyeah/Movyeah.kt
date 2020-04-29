package hexlay.movyeah

import android.app.Application
import com.rezwan.knetworklib.KNetwork
import zerobranch.androidremotedebugger.AndroidRemoteDebugger

class Movyeah : Application() {

    override fun onCreate() {
        super.onCreate()
        KNetwork.initialize(this)
        if (BuildConfig.DEBUG) {
            AndroidRemoteDebugger.init(applicationContext)
        }
    }

}