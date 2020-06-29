package hexlay.movyeah.api.alerts

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object AlertFactory {

    fun build(): AlertAPI {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AlertAPI.BASE_URL)
                .build()
                .create(AlertAPI::class.java)
    }

}