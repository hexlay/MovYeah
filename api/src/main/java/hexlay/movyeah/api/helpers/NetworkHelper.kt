package hexlay.movyeah.api.helpers

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import org.jetbrains.anko.connectivityManager

object NetworkHelper {

    fun isNetworkAvailable(context: Context): Boolean {
        return if (Constants.isAndroidM) {
            isNetworkAvailableM(context)
        } else {
            isNetworkAvailableL(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailableM(context: Context): Boolean {
        val network = context.connectivityManager.activeNetwork
        val capabilities = context.connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isNetworkAvailableL(context: Context): Boolean {
        val network = context.connectivityManager.activeNetworkInfo
        return network != null && network.isConnected
    }

}