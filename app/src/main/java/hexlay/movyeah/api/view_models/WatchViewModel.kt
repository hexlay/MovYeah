package hexlay.movyeah.api.view_models

import android.app.Application
import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.view_models.base.AbsAdjaraViewModel
import hexlay.movyeah.models.events.NetworkErrorEvent
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Actor
import hexlay.movyeah.models.movie.attributes.show.Episode
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class WatchViewModel(application: Application) : AbsAdjaraViewModel(application) {

    val movie = MutableLiveData<Movie>()
    val movieData = MutableLiveData<Episode>()
    val actorList = MutableLiveData<List<Actor>>()
    val tvShowEpisodes = MutableLiveData<SparseArray<List<Episode>>>()

    fun fetchMovieFileData(id: Int) {
        scope.launch {
            try {
                movieData.postValue(repository.getEpisodeList(id, 0)?.get(0))
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
    }

    fun fetchActors(id: Int) {
        scope.launch {
            try {
                actorList.postValue(repository.getMovieActors(id))
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
    }

    fun fetchMovie(id: Int) {
        scope.launch {
            try {
                movie.postValue(repository.getMovie(id))
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
    }

    fun fetchTvShowEpisodes(id: Int, seasons: Int) {
        val map = SparseArray<List<Episode>>()
        scope.launch {
            try {
                for (season in 1..seasons) {
                    map[season] = repository.getEpisodeList(id, season)!!
                }
                tvShowEpisodes.postValue(map)
            } catch (t: Throwable) {
                EventBus.getDefault().post(NetworkErrorEvent())
            }
        }
    }

}