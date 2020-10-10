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
        val cacheControl = if (NetworkHelper.isNetworkAvailable(context)) {
            CacheControl.Builder()
                    .maxAge(5, TimeUnit.SECONDS)
                    .build()
        } else {
            CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
        }
        val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
                .cacheControl(cacheControl)
                .build()
        return chain.proceed(request)
    }

}