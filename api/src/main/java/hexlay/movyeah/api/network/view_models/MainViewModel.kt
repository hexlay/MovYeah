package hexlay.movyeah.api.network.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AbsAdjaraViewModel(application) {

    fun fetchGeoMovies(): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getGeoMovies())
            } catch (t: Throwable) {
                Log.e("fetchGeoMovies", t.message.toString())
            }
        }
        return movies
    }

    fun fetchTopMovies(): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getTopMovies())
            } catch (t: Throwable) {
                Log.e("fetchTopMovies", t.message.toString())
            }
        }
        return movies
    }

    fun fetchTopTvShows(): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getTopTvShows())
            } catch (t: Throwable) {
                Log.e("fetchTopTvShows", t.message.toString())
            }
        }
        return movies
    }

    fun fetchGeoTvShows(): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getGeoTvShows())
            } catch (t: Throwable) {
                Log.e("fetchGeoTvShows", t.message.toString())
            }
        }
        return movies
    }

    fun fetchPremieres(): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getPremieres())
            } catch (t: Throwable) {
                Log.e("fetchPremieres", t.message.toString())
            }
        }
        return movies
    }

}