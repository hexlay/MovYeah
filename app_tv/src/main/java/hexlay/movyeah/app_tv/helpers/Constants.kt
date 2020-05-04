package hexlay.movyeah.app_tv.helpers

import android.os.Build
import java.util.*

object Constants {

    const val START_YEAR = 1920
    val END_YEAR = Calendar.getInstance().get(Calendar.YEAR)

    val isAndroidN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

}
