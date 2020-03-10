package hexlay.movyeah.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StringMapConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: Map<String, String>): String {
        return Gson().toJson(map)
    }

}