package hexlay.movyeah.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import hexlay.movyeah.database.converters.*
import hexlay.movyeah.database.dao.CategoriesDao
import hexlay.movyeah.database.dao.DownloadMoviesDao
import hexlay.movyeah.database.dao.EpisodesDao
import hexlay.movyeah.database.dao.MoviesDao
import hexlay.movyeah.models.movie.DownloadMovie
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Category
import hexlay.movyeah.models.movie.attributes.show.EpisodeCache

@Database(entities = [Movie::class, Category::class, EpisodeCache::class, DownloadMovie::class], version = 5)
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
    abstract fun downloadMovieDao(): DownloadMoviesDao

    companion object {
        @Volatile
        private var INSTANCE: AdjaraDatabase? = null

        fun getInstance(context: Context): AdjaraDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(AdjaraDatabase::class) {
                val migration3to5 = object : Migration(4, 5) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("CREATE TABLE IF NOT EXISTS `downloadmovie` (`identifier` TEXT NOT NULL, `currentSeason` INTEGER NOT NULL, `currentEpisode` INTEGER NOT NULL, `language` TEXT, `quality` TEXT, `url` TEXT, `downloadId` INTEGER NOT NULL, `dm_id` INTEGER, `dm_adjaraId` INTEGER, `dm_duration` INTEGER, `dm_year` INTEGER, `dm_watchCount` INTEGER, `dm_isTvShow` INTEGER, `dm_primaryName` TEXT, `dm_originalName` TEXT, `dm_secondaryName` TEXT, `dm_imdbUrl` TEXT, `dm_poster` TEXT, `dm_rating` TEXT, `dm_posters_data` TEXT, `dm_covers_data` TEXT, `dm_plots_data` TEXT, `dm_languages_data` TEXT, `dm_genres_data` TEXT, `dm_seasons_data` TEXT, PRIMARY KEY(`identifier`))")
                    }
                }

                val instance = Room.databaseBuilder(context, AdjaraDatabase::class.java, "adj_db")
                        .addMigrations(migration3to5)
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}