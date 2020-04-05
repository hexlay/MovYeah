package hexlay.movyeah.models.movie

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadMovie(
        @PrimaryKey var identifier: String = "",
        var currentSeason: Int = 0,
        var currentEpisode: Int = 0,
        var language: String?,
        var quality: String?,
        var url: String?,
        var downloadId: Long = 0,
        @Embedded(prefix = "dm_") var movie: Movie?
)