package hexlay.movyeah.api.github

import hexlay.movyeah.api.models.alert.BasicAlert
import retrofit2.Response
import retrofit2.http.GET

// Since i'm poor guy, let's use github's raw :D :D
interface AlertAPI {

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/hexlay/MovYeah/master/"
    }

    @GET("ALERTS")
    suspend fun getBasicAlertsAsync(): Response<List<BasicAlert>>

}