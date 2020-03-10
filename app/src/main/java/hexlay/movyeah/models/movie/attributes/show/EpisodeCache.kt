package hexlay.movyeah.models.movie.attributes.show

import androidx.room.Entity

@Entity(primaryKeys = ["movieId", "episode", "season"])
data class EpisodeCache(
        var movieId: Int = 0,
        var episode: Int = 0,
        var season: Int = 0
)