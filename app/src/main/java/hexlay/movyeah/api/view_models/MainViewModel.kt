package hexlay.movyeah.api.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.view_models.base.AbsAdjaraViewModel
import hexlay.movyeah.models.events.NetworkErrorEvent
import hexlay.movyeah.models.movie.Movie
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

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
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
    }

}