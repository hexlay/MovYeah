package hexlay.movyeah.app_tv.models

import android.os.Parcelable
import android.util.SparseArray
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.Subtitle
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.api.models.attributes.show.EpisodeFileData
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
        var currentSeason: Int,
        var currentEpisode: Int
) : Parcelable