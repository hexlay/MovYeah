package movyeahtv.models

import android.os.Parcelable
import android.util.SparseArray
import hexlay.movyeah.models.movie.Movie
import hexlay.movyeah.models.movie.attributes.Subtitle
import hexlay.movyeah.models.movie.attributes.show.Episode
import hexlay.movyeah.models.movie.attributes.show.EpisodeFileData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaybackModel(
        var movie: Movie,
        var fileData: Map<String, List<EpisodeFileData>>,
        var subtitleData: Map<String, List<Subtitle>>,
        var tvShowSeasons: SparseArray<List<Episode>>,
        var qualityKey: String,
        var languageKey: String,
        var subtitleKey: String,
        var currentSeason: Int
) : Parcelable