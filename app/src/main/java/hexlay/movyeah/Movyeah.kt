package hexlay.movyeah

import android.app.Application
import com.rezwan.knetworklib.KNetwork

class Movyeah : Application() {

    override fun onCreate() {
        super.onCreate()
        KNetwork.initialize(this)
    }

}