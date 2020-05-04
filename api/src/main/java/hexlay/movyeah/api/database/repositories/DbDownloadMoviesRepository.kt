package hexlay.movyeah.api.database.repositories

import android.content.Context
import hexlay.movyeah.api.database.AdjaraDatabase
import hexlay.movyeah.api.database.dao.DownloadMoviesDao
import hexlay.movyeah.api.models.DownloadMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbDownloadMoviesRepository(context: Context) : CoroutineScope {

    override val coroutineContext
        get() = Dispatchers.Main

    private var dao: DownloadMoviesDao? = null

    init {
        dao = AdjaraDatabase.getInstance(context).downloadMovieDao()
    }

    fun getMovies() = dao?.getAll()

    fun getMovie(id: String) = dao?.getOne(id)

    fun insertMovie(movie: DownloadMovie) {
        launch {
            insert(movie)
        }
    }

    fun deleteMovie(movie: DownloadMovie) {
        launch {
            delete(movie)
        }
    }

    private suspend fun insert(movie: DownloadMovie) {
        withContext(Dispatchers.IO) {
            dao?.insert(movie)
        }
    }

    private suspend fun delete(movie: DownloadMovie) {
        withContext(Dispatchers.IO) {
            dao?.delete(movie)
        }
    }

}