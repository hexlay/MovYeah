package hexlay.movyeah.api.network

import android.content.Context
import hexlay.movyeah.api.BuildConfig
import hexlay.movyeah.api.network.interceptors.ConnectionInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object AdjaraFactory {

    fun build(context: Context): AdjaraAPI {
        val size = 5 * 1024 * 1024
        val cache = Cache(context.cacheDir, size.toLong())
        val client = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(ConnectionInterceptor(context))
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            client.addInterceptor(logging)
        }
        return Retrofit.Builder()
                .client(client.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AdjaraAPI.BASE_URL)
                .build()
                .create(AdjaraAPI::class.java)
    }

}