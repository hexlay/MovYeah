package hexlay.movyeah.models.movie

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadMovie(
        @PrimaryKey var id: Int = 0,
        var url: String?,
        var downloadId: Long = 0,
        @Embedded(prefix = "dm_") var movie: Movie?
) {

    override fun equals(other: Any?): Boolean {
        if (other is DownloadMovie) {
            return movie?.id == other.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = url?.hashCode() ?: 0
        result = 31 * result + (downloadId.hashCode())
        result = 31 * result + (movie?.hashCode() ?: 0)
        result = 31 * result + id
        return result
    }

}