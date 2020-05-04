package hexlay.movyeah.api.network.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AbsAdjaraViewModel(application) {

    val geoMovies = MutableLiveData<List<Movie>>()
    val topMovies = MutableLiveData<List<Movie>>()
    val topTvShows = MutableLiveData<List<Movie>>()
    val geoTvShows = MutableLiveData<List<Movie>>()
    val premieres = MutableLiveData<List<Movie>>()

    fun fetchMovies() {
        scope.launch {
            try {
                geoMovies.postValue(repository.getGeoMovies())
                topMovies.postValue(repository.getTopMovies())
                topTvShows.postValue(repository.getTopTvShows())
                geoTvShows.postValue(repository.getGeoTvShows())
                premieres.postValue(repository.getPremieres())
            } catch (t: Throwable) {}
        }
    }

}