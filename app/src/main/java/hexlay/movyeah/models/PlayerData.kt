package hexlay.movyeah.models

import android.os.Parcelable
import hexlay.movyeah.api.models.attributes.Subtitle
import hexlay.movyeah.api.models.attributes.show.EpisodeFileData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayerData(
        var movieId: String,
        var movieName: String,
        var fileData: Map<String, List<EpisodeFileData>> = HashMap(),
        var subtitleData: Map<String, List<Subtitle>> = HashMap(),
        var episode: Int = 0,
        var offlineIdentifier: String? = null,
) : Parcelable