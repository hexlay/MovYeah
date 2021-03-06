package hexlay.movyeah.api.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hexlay.movyeah.api.models.DownloadMovie

@Dao
interface DownloadMoviesDao {

    @Query("SELECT * FROM downloadmovie")
    fun getAll(): LiveData<List<DownloadMovie>>

    @Query("SELECT * FROM downloadmovie WHERE identifier = :id")
    fun getOne(id: String): LiveData<DownloadMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: DownloadMovie)

    @Delete
    fun delete(movie: DownloadMovie)

}