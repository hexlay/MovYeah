package hexlay.movyeah.helpers

import android.os.Build
import android.os.Environment
import java.util.*

object Constants {

    val DOWNLOAD_DIRECTORY: String = if (isAndroidR) {
        Environment.DIRECTORY_MOVIES
    } else {
        Environment.DIRECTORY_DOWNLOADS
    }

    val START_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 100
    val END_YEAR = Calendar.getInstance().get(Calendar.YEAR)

    const val RECYCLER_GRID_COUNT = 3

    val isAndroidN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    val isAndroidN_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

    val isAndroidO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val isAndroidQ: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    val isAndroidR: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

}
