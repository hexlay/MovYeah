package hexlay.movyeah.api.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.api.models.attributes.Country

object CountryConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<Country> {
        val mapType = object : TypeToken<List<Country>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: List<Country>): String {
        return Gson().toJson(map)
    }

}