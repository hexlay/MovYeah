package hexlay.movyeah.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hexlay.movyeah.models.movie.DownloadMovie

@Dao
interface DownloadMoviesDao {

    @Query("SELECT * FROM downloadmovie")
    fun getAll(): LiveData<List<DownloadMovie>>

    @Query("SELECT * FROM downloadmovie WHERE id = :id")
    fun getOne(id: Int): LiveData<DownloadMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: DownloadMovie)

    @Delete
    fun delete(movie: DownloadMovie)

}