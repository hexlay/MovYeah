package hexlay.movyeah.helpers

import android.os.Build
import android.os.Environment
import java.util.*

object Constants {

    const val SHORTCUT_ACTION = "START_MOVIE_FROM_SHORTCUT"

    val DOWNLOAD_DIRECTORY = Environment.DIRECTORY_DOWNLOADS

    const val START_YEAR = 1920
    val END_YEAR = Calendar.getInstance().get(Calendar.YEAR)

    // PIP
    const val CONTROL_TYPE_PLAY = 1
    const val CONTROL_TYPE_PAUSE = 2

    val isAndroidO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val isAndroidN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    val isAndroidN_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

    val isAndroidQ: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

}
