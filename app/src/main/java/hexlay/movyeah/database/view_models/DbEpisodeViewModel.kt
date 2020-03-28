package hexlay.movyeah.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.database.repositories.DbEpisodesRepository
import hexlay.movyeah.models.movie.attributes.show.EpisodeCache

class DbEpisodeViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbEpisodesRepository(application.applicationContext)

    fun getEpisode(id: Int) = repository.getEpisode(id)

    fun insertEpisode(episodeCache: EpisodeCache) = repository.insertEpisode(episodeCache)

}