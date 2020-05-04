package hexlay.movyeah.api.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.database.repositories.DbEpisodesRepository
import hexlay.movyeah.api.models.attributes.show.EpisodeCache

class DbEpisodeViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbEpisodesRepository(application.applicationContext)

    fun getEpisode(id: Int) = repository.getEpisode(id)

    fun insertEpisode(episodeCache: EpisodeCache) = repository.insertEpisode(episodeCache)

}