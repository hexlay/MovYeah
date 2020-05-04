package hexlay.movyeah.api.helpers

import android.app.Activity
import androidx.fragment.app.Fragment

fun <T> List<T>.toCommaList(): String = joinToString(separator = ", ")

fun Activity.isNetworkAvailable(): Boolean {
    return NetworkHelper.isNetworkAvailable(baseContext)
}

fun Fragment.isNetworkAvailable(): Boolean {
    return requireActivity().isNetworkAvailable()
}