package hexlay.movyeah

import android.app.Application
import zerobranch.androidremotedebugger.AndroidRemoteDebugger

class Movyeah : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            AndroidRemoteDebugger.init(applicationContext)
        }
    }

}