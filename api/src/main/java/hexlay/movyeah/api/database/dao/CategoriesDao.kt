package hexlay.movyeah.api.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hexlay.movyeah.api.models.attributes.Category

@Dao
interface CategoriesDao {

    @Query("SELECT * FROM category")
    fun getAll(): LiveData<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("DELETE FROM category")
    fun clear()

}