package hexlay.movyeah.api.network

import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Actor
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.api.models.attributes.Country
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.models.generics.GenericList
import hexlay.movyeah.api.models.helpers.ExtendedMovieHelper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AdjaraAPI {

    companion object {
        const val BASE_URL = "https://api.adjaranet.com/api/v1/"
    }

    @GET("genres?per_page=100&source=adjaranet")
    suspend fun getCategoriesAsync(): Response<GenericList<Category>>

    @GET("countries?per_page=300&source=adjaranet")
    suspend fun getCountriesAsync(): Response<GenericList<Country>>

    @GET("movies")
    suspend fun getMainMoviesAsync(
            @Query("page") page: Int = 1,
            @Query("per_page") perPage: Int = 30,
            @Query("filters[type]") filtersType: String = "movie",
            @Query("filters[language]") filtersLanguage: String? = null,
            @Query("filters[genre]") filtersGenres: String? = null,
            @Query("filters[subtitles]") filtersSubtitles: String? = null,
            @Query("filters[country]") filtersCountry: String? = null,
            @Query("filters[year_range]") filtersYears: String,
            @Query("filters[init]") filtersInit: String = "true",
            @Query("filters[sort]") filtersSort: String = "-upload_date",
            @Query("filters[with_actors]") filtersActors: Int = 3,
            @Query("filters[with_directors]") filtersDirectors: Int = 1,
            @Query("filters[with_files]") filtersFiles: String = "yes",
            @Query("sort") sort: String = filtersSort,
            @Query("source") source: String = "adjaranet"
    ): Response<GenericList<Movie>>

    @GET("search-advanced")
    suspend fun searchMoviesAsync(
            @Query("keywords") keywords: String,
            @Query("filters[keyword]") filterKeyword: String = keywords,
            @Query("filters[type]") filterType: String = "movie",
            @Query("filters[init]") filtersInit: String = "true",
            @Query("filters[with_actors]") filtersActors: Int = 3,
            @Query("filters[with_directors]") filtersDirectors: Int = 1,
            @Query("filters[with_files]") filtersFiles: String = "yes",
            @Query("page") page: Int = 1,
            @Query("per_page") perPage: Int = 30,
            @Query("source") source: String = "adjaranet"
    ): Response<GenericList<Movie>>

    @GET("movies/{id}/related")
    suspend fun getRelatedMoviesAsync(
            @Path("id") id: Int,
            @Query("filters[with_actors]") filtersActors: Int = 3,
            @Query("filters[with_directors]") filtersDirectors: Int = 1,
            @Query("page") page: Int = 1,
            @Query("per_page") perPage: Int = 20,
            @Query("source") source: String = "adjaranet"
    ): Response<GenericList<Movie>>

    @GET("movies/premiere-day?page=1&per_page=10&filters=&source=adjaranet")
    suspend fun getPremieresAsync(): Response<GenericList<Movie>>

    @GET("movies/top?type=movie&period=day&page=1&per_page=10&filters[with_actors]=3&filters[with_files]=yes&filters[with_directors]=1&source=adjaranet")
    suspend fun getTopMoviesAsync(): Response<GenericList<Movie>>

    @GET("movies?page=1&per_page=10&filters[language]=GEO&filters[type]=movie&filters[with_actors]=3&filters[with_directors]=1&filters[with_files]=yes&sort=-upload_date&source=adjaranet")
    suspend fun getGeoMoviesAsync(): Response<GenericList<Movie>>

    @GET("movies/top?type=series&period=day&page=1&per_page=10&filters[with_actors]=3&filters[with_files]=yes&filters[with_directors]=1&source=adjaranet")
    suspend fun getTopTvShowsAsync(): Response<GenericList<Movie>>

    @GET("movies?page=1&per_page=10&filters[language]=GEO&filters[type]=series&filters[with_actors]=3&filters[with_directors]=1&filters[with_files]=yes&sort=-upload_date&source=adjaranet")
    suspend fun getGeoTvShowsAsync(): Response<GenericList<Movie>>

    @GET("casts/{id}/movies")
    suspend fun getActorMoviesAsync(
            @Path("id") id: Int,
            @Query("page") page: Int = 1,
            @Query("per_page") perPage: Int = 30,
            @Query("sort") sort: String = "-year",
            @Query("filters[with_actors]") filtersActors: Int = 3,
            @Query("filters[with_directors]") filtersDirectors: Int = 1,
            @Query("source") source: String = "adjaranet"
    ): Response<GenericList<Movie>>

    @GET("movies/{id}?filters[with_directors]=3&source=adjaranet")
    suspend fun getMovieInfoAsync(
            @Path("id") id: Int
    ): Response<ExtendedMovieHelper>

    @GET("movies/{id}/persons?page=1&per_page=100&filters[role]=cast&source=adjaranet")
    suspend fun getMovieActorsAsync(
            @Path("id") id: Int
    ): Response<GenericList<Actor>>

    @GET("movies/{id}/season-files/{season}?source=adjaranet")
    suspend fun getEpisodeListAsync(
            @Path("id") id: Int,
            @Path("season") season: Int
    ): Response<GenericList<Episode>>

}