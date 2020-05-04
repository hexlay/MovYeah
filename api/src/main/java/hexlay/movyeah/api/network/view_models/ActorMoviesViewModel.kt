package hexlay.movyeah.api.network.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class ActorMoviesViewModel(application: Application) : AbsAdjaraViewModel(application) {

    fun fetchMovies(id: Int, page: Int = 1): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getActorMovies(id, page))
            } catch (t: Throwable) {}
        }
        return movies
    }

}