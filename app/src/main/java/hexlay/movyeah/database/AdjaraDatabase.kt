package hexlay.movyeah.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hexlay.movyeah.database.converters.*
import hexlay.movyeah.database.dao.CategoriesDao
import hexlay.movyeah.database.dao.EpisodesDao
import hexlay.movyeah.database.dao.MoviesDao
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Category
import hexlay.movyeah.models.movie.attributes.show.EpisodeCache

@Database(entities = [Movie::class, Category::class, EpisodeCache::class], version = 4)
@TypeConverters(
        StringMapConverter::class,
        RatingMapConverter::class,
        PlotConverter::class,
        LanguageConverter::class,
        CategoryConverter::class,
        SeasonConverter::class
)
abstract class AdjaraDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun episodesDao(): EpisodesDao

    companion object {
        @Volatile
        private var INSTANCE: AdjaraDatabase? = null

        fun getInstance(context: Context): AdjaraDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(AdjaraDatabase::class) {
                val instance = Room.databaseBuilder(context, AdjaraDatabase::class.java, "adj_db")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}