package hexlay.movyeah.api.database.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import hexlay.movyeah.api.database.repositories.DbDownloadMoviesRepository
import hexlay.movyeah.api.models.DownloadMovie

class  DbDownloadMovieViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = DbDownloadMoviesRepository(application.applicationContext)

    fun getMovies() = repository.getMovies()

    fun getMovie(id: String) = repository.getMovie(id)

    fun insertMovie(movie: DownloadMovie) = repository.insertMovie(movie)

    fun deleteMovie(movie: DownloadMovie) = repository.deleteMovie(movie)

}