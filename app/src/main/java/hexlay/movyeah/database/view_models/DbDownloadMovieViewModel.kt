package hexlay.movyeah.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.database.repositories.DbDownloadMoviesRepository
import hexlay.movyeah.models.movie.DownloadMovie

class  DbDownloadMovieViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbDownloadMoviesRepository(application.applicationContext)

    fun getMovies() = repository.getMovies()

    fun getMovie(id: String) = repository.getMovie(id)

    fun insertMovie(movie: DownloadMovie) = repository.insertMovie(movie)

    fun deleteMovie(movie: DownloadMovie) = repository.deleteMovie(movie)

}