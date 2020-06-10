package hexlay.movyeah.api.network.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class ActorMoviesViewModel(application: Application) : AbsAdjaraViewModel(application) {

    val movies = MutableLiveData<List<Movie>>()

    fun fetchActorMovies(id: Int, page: Int = 1) {
        scope.launch {
            try {
                movies.postValue(repository.getActorMovies(id, page))
            } catch (t: Throwable) {
                Log.e("fetchActorMovies", t.message.toString())
            }
        }
    }

}