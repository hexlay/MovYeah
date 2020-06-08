package hexlay.movyeah.api.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hexlay.movyeah.api.models.attributes.Country

@Dao
interface CountriesDao {

    @Query("SELECT * FROM country")
    fun getAll(): LiveData<List<Country>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(country: Country)

    @Query("DELETE FROM country")
    fun clear()

}