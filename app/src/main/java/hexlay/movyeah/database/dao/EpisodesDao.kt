package hexlay.movyeah.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hexlay.movyeah.models.movie.attributes.show.EpisodeCache

@Dao
interface EpisodesDao {

    @Query("SELECT * FROM episodecache")
    fun getAll(): LiveData<List<EpisodeCache>>

    @Query("SELECT * FROM episodecache WHERE movieId = :id")
    fun getOne(id: Int): LiveData<EpisodeCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: EpisodeCache)

    @Query("DELETE FROM episodecache WHERE movieId = :id")
    fun clear(id: Int)

}