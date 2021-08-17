package hexlay.movyeah.api.github

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object GithubFactory {

    fun buildAlerts(): AlertAPI {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AlertAPI.BASE_URL)
                .build()
                .create(AlertAPI::class.java)
    }

    fun buildApiCall(): GithubAPI {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(GithubAPI.BASE_URL)
            .build()
            .create(GithubAPI::class.java)
    }

}