package hexlay.movyeah.api.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hexlay.movyeah.api.models.Movie

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movie")
    fun getAll(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie WHERE id = :id")
    fun getOne(id: Int): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)

    @Delete
    fun delete(movie: Movie)

}