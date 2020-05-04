package hexlay.movyeah.api.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.api.models.attributes.Category

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