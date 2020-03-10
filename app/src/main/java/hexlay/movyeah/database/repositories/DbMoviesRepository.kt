package hexlay.movyeah.database.repositories

import android.content.Context
import hexlay.movyeah.database.AdjaraDatabase
import hexlay.movyeah.database.dao.MoviesDao
import hexlay.movyeah.models.movie.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DbMoviesRepository(context: Context) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var dao: MoviesDao? = null

    init {
        dao = AdjaraDatabase.getInstance(context).moviesDao()
    }

    fun getMovies() = dao?.getAll()

    fun getMovie(id: Int) = dao?.getOne(id)

    fun insertMovie(movie: Movie) {
        launch {
            insert(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        launch {
            delete(movie)
        }
    }

    private suspend fun insert(movie: Movie) {
        withContext(Dispatchers.IO) {
            dao?.insert(movie)
        }
    }

    private suspend fun delete(movie: Movie) {
        withContext(Dispatchers.IO) {
            dao?.delete(movie)
        }
    }

}