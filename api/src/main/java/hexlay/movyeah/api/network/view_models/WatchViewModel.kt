package hexlay.movyeah.api.network.view_models

import android.app.Application
import android.util.SparseArray
import androidx.core.util.set
import androidx.lifecycle.MutableLiveData
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Actor
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.network.view_models.base.AbsAdjaraViewModel
import kotlinx.coroutines.launch

class WatchViewModel(application: Application) : AbsAdjaraViewModel(application) {

    fun fetchMovieFileData(id: Int): MutableLiveData<Episode> {
        val movieData = MutableLiveData<Episode>()
        scope.launch {
            try {
                movieData.postValue(repository.getEpisodeList(id, 0)?.get(0))
            } catch (t: Throwable) {}
        }
        return movieData
    }

    fun fetchActors(id: Int): MutableLiveData<List<Actor>> {
        val actorList = MutableLiveData<List<Actor>>()
        scope.launch {
            try {
                actorList.postValue(repository.getMovieActors(id))
            } catch (t: Throwable) {}
        }
        return actorList
    }

    fun fetchMovie(id: Int): MutableLiveData<Movie> {
        val movie = MutableLiveData<Movie>()
        scope.launch {
            try {
                movie.postValue(repository.getMovie(id))
            } catch (t: Throwable) {}
        }
        return movie
    }

    fun fetchTvShowEpisodes(id: Int, seasons: Int): MutableLiveData<SparseArray<List<Episode>>> {
        val map = SparseArray<List<Episode>>()
        val tvShowEpisodes = MutableLiveData<SparseArray<List<Episode>>>()
        scope.launch {
            try {
                for (season in 1..seasons) {
                    map[season] = repository.getEpisodeList(id, season)!!
                }
                tvShowEpisodes.postValue(map)
            } catch (t: Throwable) {}
        }
        return tvShowEpisodes
    }

}