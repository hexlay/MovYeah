package hexlay.movyeah.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.models.movie.attributes.Category
import hexlay.movyeah.models.movie.attributes.Plot
import hexlay.movyeah.models.movie.attributes.Rating

object CategoryConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<Category> {
        val mapType = object : TypeToken<List<Category>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: List<Category>): String {
        return Gson().toJson(map)
    }

}