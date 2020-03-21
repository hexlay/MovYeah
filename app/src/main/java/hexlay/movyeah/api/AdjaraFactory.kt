package hexlay.movyeah.api

import android.content.Context
import hexlay.movyeah.api.interceptors.ConnectionInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import zerobranch.androidremotedebugger.logging.NetLoggingInterceptor

object AdjaraFactory {

    private fun buildRetrofit(context: Context): Retrofit {
        val cache = Cache(context.cacheDir, 5242880) // 5 * 1024 * 1024
        val client = OkHttpClient.Builder()
                .addInterceptor(NetLoggingInterceptor())
                .addInterceptor(ConnectionInterceptor(context))
                .cache(cache)
                .build()
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AdjaraAPI.BASE_URL)
                .build()
    }

    fun createService(context: Context): AdjaraAPI {
        return buildRetrofit(context).create(AdjaraAPI::class.java)
    }

}