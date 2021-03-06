package hexlay.movyeah.api.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.database.repositories.DbMoviesRepository
import hexlay.movyeah.api.models.Movie

class  DbMovieViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbMoviesRepository(application.applicationContext)

    fun getMovies() = repository.getMovies()

    fun getMovie(id: Int) = repository.getMovie(id)

    fun insertMovie(movie: Movie) = repository.insertMovie(movie)

    fun deleteMovie(movie: Movie) = repository.deleteMovie(movie)

}