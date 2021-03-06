package hexlay.movyeah.api.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.api.models.attributes.Season

object SeasonConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<Season> {
        val mapType = object : TypeToken<List<Season>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: List<Season>): String {
        return Gson().toJson(map)
    }

}