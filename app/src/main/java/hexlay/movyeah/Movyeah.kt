package hexlay.movyeah

import android.app.Application
import com.rezwan.knetworklib.KNetwork
import hexlay.movyeah.helpers.PreferenceHelper

class Movyeah : Application() {

    override fun onCreate() {
        super.onCreate()
        KNetwork.initialize(this)
        PreferenceHelper.makePreferences(applicationContext)
    }

}