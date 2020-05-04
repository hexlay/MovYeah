package hexlay.movyeah.api.network.repositories

import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.repositories.base.AbsAdjaraRepository
import hexlay.movyeah.api.models.attributes.Actor
import hexlay.movyeah.api.models.attributes.show.Episode

class AdjaraRepository(private val api: hexlay.movyeah.api.network.AdjaraAPI) : AbsAdjaraRepository() {

    suspend fun getGeoMovies(): List<Movie>? {
        val response = safeApiCall(
                call = { api.getGeoMoviesAsync() },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getTopMovies(): List<Movie>? {
        val response = safeApiCall(
                call = { api.getTopMoviesAsync() },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getTopTvShows(): List<Movie>? {
        val response = safeApiCall(
                call = { api.getTopTvShowsAsync() },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getGeoTvShows(): List<Movie>? {
        val response = safeApiCall(
                call = { api.getGeoTvShowsAsync() },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getPremieres(): List<Movie>? {
        val response = safeApiCall(
                call = { api.getPremieresAsync() },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getMainMovies(
            page: Int = 1,
            filtersType: String = "movie",
            filtersLanguage: String? = null,
            filtersGenres: String? = null,
            filtersYears: String,
            filtersSort: String = "-upload_date"
    ): List<Movie>? {
        val response = safeApiCall(
                call = {
                    api.getMainMoviesAsync(
                            page = page,
                            filtersType = filtersType,
                            filtersLanguage = filtersLanguage,
                            filtersGenres = filtersGenres,
                            filtersYears = filtersYears,
                            filtersSort = filtersSort
                    )
                },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getActorMovies(id: Int, page: Int = 1): List<Movie>? {
        val response = safeApiCall(
                call = { api.getActorMoviesAsync(id, page = page) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getEpisodeList(id: Int, season: Int): List<Episode>? {
        val response = safeApiCall(
                call = { api.getEpisodeListAsync(id, season) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getMovieActors(id: Int): List<Actor>? {
        val response = safeApiCall(
                call = { api.getMovieActorsAsync(id) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun searchMovie(page: Int, keywords: String): List<Movie>? {
        val response = safeApiCall(
                call = { api.searchMoviesAsync(page = page, keywords = keywords) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getRelated(id: Int): List<Movie>? {
        val response = safeApiCall(
                call = { api.getRelatedMoviesAsync(id) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

    suspend fun getMovie(id: Int): Movie? {
        val response = safeApiCall(
                call = { api.getMovieInfoAsync(id) },
                errorMessage = "Error fetching movies"
        )
        return response?.data
    }

}