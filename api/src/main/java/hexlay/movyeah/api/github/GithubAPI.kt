package hexlay.movyeah.api.github

import hexlay.movyeah.api.models.github.Release
import retrofit2.Response
import retrofit2.http.GET

interface GithubAPI {

    companion object {
        const val BASE_URL = "https://api.github.com/"
    }

    @GET("repos/hexlay/MovYeah/releases")
    suspend fun getReleasesAsync(): Response<List<Release>>

}