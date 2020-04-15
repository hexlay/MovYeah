package hexlay.movyeah.helpers

import android.os.Build
import java.util.*

object Constants {

    const val START_YEAR = 1920
    val END_YEAR = Calendar.getInstance().get(Calendar.YEAR)

    // PIP
    const val CONTROL_TYPE_PLAY = 1
    const val CONTROL_TYPE_PAUSE = 2

    val isAndroidO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val isAndroidN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    val isAndroidQ: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

}
