package hexlay.movyeah.api.github.repositories

import hexlay.movyeah.api.github.GithubAPI
import hexlay.movyeah.api.models.github.Release
import hexlay.movyeah.api.network.repositories.base.AbsAdjaraRepository

class GithubRepository(private val api: GithubAPI) : AbsAdjaraRepository() {

    suspend fun getReleases(): List<Release>? {
        return safeApiCall { api.getReleasesAsync() }
    }

}