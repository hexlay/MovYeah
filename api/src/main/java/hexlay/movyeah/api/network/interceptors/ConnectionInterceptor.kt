package hexlay.movyeah.api.network.interceptors

import android.content.Context
import hexlay.movyeah.api.helpers.NetworkHelper
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class ConnectionInterceptor(private val context: Context) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetworkHelper.isNetworkAvailable(context)) {
            val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
        }
        return chain.proceed(request)
    }

}