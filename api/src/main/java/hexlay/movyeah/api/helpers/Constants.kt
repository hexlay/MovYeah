package hexlay.movyeah.api.helpers

import android.os.Build

object Constants {

    val isAndroidM: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

}