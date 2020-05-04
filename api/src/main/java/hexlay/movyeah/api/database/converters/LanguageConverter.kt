package hexlay.movyeah.api.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hexlay.movyeah.api.models.attributes.Language

object LanguageConverter {

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<Language> {
        val mapType = object : TypeToken<List<Language>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringMap(map: List<Language>): String {
        return Gson().toJson(map)
    }

}