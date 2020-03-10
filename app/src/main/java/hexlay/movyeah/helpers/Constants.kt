package hexlay.movyeah.helpers

import android.os.Build

object Constants {

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
