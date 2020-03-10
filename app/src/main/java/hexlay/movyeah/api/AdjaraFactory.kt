package hexlay.movyeah.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import zerobranch.androidremotedebugger.logging.NetLoggingInterceptor

object AdjaraFactory {

    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
                .addInterceptor(NetLoggingInterceptor())
                .build()
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(AdjaraAPI.BASE_URL)
                .build()
    }

    fun createService(): AdjaraAPI {
        return buildRetrofit().create(AdjaraAPI::class.java)
    }

}