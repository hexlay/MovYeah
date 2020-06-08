package hexlay.movyeah.api.network.repositories

import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Actor
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.api.models.attributes.Country
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.network.AdjaraAPI
import hexlay.movyeah.api.network.repositories.base.AbsAdjaraRepository

class AdjaraRepository(private val api: AdjaraAPI) : AbsAdjaraRepository() {

    suspend fun getCategories(): List<Category>? {
        val response = safeApiCall { api.getCategoriesAsync() }
        return response?.data
    }

    suspend fun getCountries(): List<Country>? {
        val response = safeApiCall { api.getCountriesAsync() }
        return response?.data
    }

    suspend fun getGeoMovies(): List<Movie>? {
        val response = safeApiCall { api.getGeoMoviesAsync() }
        return response?.data
    }

    suspend fun getTopMovies(): List<Movie>? {
        val response = safeApiCall { api.getTopMoviesAsync() }
        return response?.data
    }

    suspend fun getTopTvShows(): List<Movie>? {
        val response = safeApiCall { api.getTopTvShowsAsync() }
        return response?.data
    }

    suspend fun getGeoTvShows(): List<Movie>? {
        val response = safeApiCall { api.getGeoTvShowsAsync() }
        return response?.data
    }

    suspend fun getPremieres(): List<Movie>? {
        val response = safeApiCall { api.getPremieresAsync() }
        return response?.data
    }

    suspend fun getMainMovies(
            page: Int = 1,
            filtersType: String = "movie",
            filtersLanguage: String? = null,
            filtersGenres: String? = null,
            filtersCountries: String? = null,
            filtersYears: String,
            filtersSort: String = "-upload_date"
    ): List<Movie>? {
        val response = safeApiCall {
            api.getMainMoviesAsync(
                    page = page,
                    filtersType = filtersType,
                    filtersLanguage = filtersLanguage,
                    filtersGenres = filtersGenres,
                    filtersCountry = filtersCountries,
                    filtersYears = filtersYears,
                    filtersSort = filtersSort
            )
        }
        return response?.data
    }

    suspend fun getActorMovies(id: Int, page: Int = 1): List<Movie>? {
        val response = safeApiCall { api.getActorMoviesAsync(id, page = page) }
        return response?.data
    }

    suspend fun getEpisodeList(id: Int, season: Int): List<Episode>? {
        val response = safeApiCall { api.getEpisodeListAsync(id, season) }
        return response?.data
    }

    suspend fun getMovieActors(id: Int): List<Actor>? {
        val response = safeApiCall { api.getMovieActorsAsync(id) }
        return response?.data
    }

    suspend fun searchMovie(page: Int, keywords: String): List<Movie>? {
        val response = safeApiCall { api.searchMoviesAsync(page = page, keywords = keywords) }
        return response?.data
    }

    suspend fun getRelated(id: Int): List<Movie>? {
        val response = safeApiCall { api.getRelatedMoviesAsync(id) }
        return response?.data
    }

    suspend fun getMovie(id: Int): Movie? {
        val response = safeApiCall { api.getMovieInfoAsync(id) }
        return response?.data
    }

}