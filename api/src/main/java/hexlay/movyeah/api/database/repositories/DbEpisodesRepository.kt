package hexlay.movyeah.api.database.repositories

import android.content.Context
import hexlay.movyeah.api.database.AdjaraDatabase
import hexlay.movyeah.api.database.dao.EpisodesDao
import hexlay.movyeah.api.models.attributes.show.EpisodeCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbEpisodesRepository(context: Context) : CoroutineScope {

    override val coroutineContext
        get() = Dispatchers.Main

    private var dao: EpisodesDao? = null

    init {
        dao = AdjaraDatabase.getInstance(context).episodesDao()
    }

    fun getEpisode(id: Int) = dao?.getOne(id)

    fun insertEpisode(episodeCache: EpisodeCache) {
        launch {
            insert(episodeCache)
        }
    }

    private suspend fun insert(episodeCache: EpisodeCache) {
        withContext(Dispatchers.IO) {
            dao?.clear(episodeCache.movieId)
        }
        withContext(Dispatchers.IO) {
            dao?.insert(episodeCache)
        }
    }

}