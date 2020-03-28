package hexlay.movyeah.api.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.view_models.base.AbsAdjaraViewModel
import hexlay.movyeah.models.movie.Movie
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