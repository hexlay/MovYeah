package hexlay.movyeah.api.view_models

import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.view_models.base.AbsAdjaraViewModel
import hexlay.movyeah.models.events.NetworkErrorEvent
import hexlay.movyeah.models.movie.Movie
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class ActorMoviesViewModel : AbsAdjaraViewModel() {

    fun fetchMovies(id: Int, page: Int = 1): MutableLiveData<List<Movie>> {
        val movies = MutableLiveData<List<Movie>>()
        scope.launch {
            try {
                movies.postValue(repository.getActorMovies(id, page))
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
        return movies
    }

}