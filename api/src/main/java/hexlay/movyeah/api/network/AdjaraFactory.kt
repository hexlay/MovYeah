package hexlay.movyeah.api.network

import android.content.Context
import hexlay.movyeah.api.network.interceptors.ConnectionInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object AdjaraFactory {

    fun build(context: Context): AdjaraAPI {
        val cache = Cache(context.cacheDir, 5 * 1024 * 1024)
        val client = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(ConnectionInterceptor(context))
                .build()
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AdjaraAPI.BASE_URL)
                .build()
                .create(AdjaraAPI::class.java)
    }

}