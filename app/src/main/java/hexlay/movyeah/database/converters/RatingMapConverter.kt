package hexlay.movyeah.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.models.movie.attributes.Rating

object RatingMapConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): Map<String, Rating> {
        val mapType = object : TypeToken<Map<String, Rating>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: Map<String, Rating>): String {
        return Gson().toJson(map)
    }

}