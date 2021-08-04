package hexlay.movyeah.api.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
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