package hexlay.movyeah.api.network.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class MovieListViewModelTv(application: Application) : AbsAdjaraViewModel(application) {

    val movies = MutableLiveData<List<Movie>>()
    val shows = MutableLiveData<List<Movie>>()

    fun fetchMainMovies(
            page: Int = 1,
            filtersSort: String = "-upload_date",
            filtersType: String = "movie",
            filtersLanguage: String? = null,
            filtersGenres: String? = null,
            filtersCountries: String? = null,
            filtersYears: String
    ) {
        scope.launch {
            try {
                movies.postValue(repository.getMainMovies(
                        page = page,
                        filtersType = filtersType,
                        filtersLanguage = filtersLanguage,
                        filtersGenres = filtersGenres,
                        filtersCountries = filtersCountries,
                        filtersYears = filtersYears,
                        filtersSort = filtersSort
                ))
            } catch (t: Throwable) {
                Log.e("fetchMainMovies", t.message.toString())
            }
        }
    }

    fun fetchMainShows(
            page: Int = 1,
            filtersSort: String = "-upload_date",
            filtersType: String = "series",
            filtersLanguage: String? = null,
            filtersGenres: String? = null,
            filtersCountries: String? = null,
            filtersYears: String
    ) {
        scope.launch {
            try {
                shows.postValue(repository.getMainMovies(
                        page = page,
                        filtersType = filtersType,
                        filtersLanguage = filtersLanguage,
                        filtersGenres = filtersGenres,
                        filtersCountries = filtersCountries,
                        filtersYears = filtersYears,
                        filtersSort = filtersSort
                ))
            } catch (t: Throwable) {
                Log.e("fetchMainMovies", t.message.toString())
            }
        }
    }

    fun fetchSearchMovie(page: Int = 1, keywords: String) {
        scope.launch {
            try {
                movies.postValue(repository.searchMovie(page, keywords))
            } catch (t: Throwable) {
                Log.e("fetchSearchMovie", t.message.toString())
            }
        }
    }

    fun fetchRelated(id: Int): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getRelated(id))
            } catch (t: Throwable) {
                Log.e("fetchRelated", t.message.toString())
            }
        }
        return movies
    }

}