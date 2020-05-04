package hexlay.movyeah.api.models.attributes.show

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.api.models.attributes.Subtitle
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeFile(
        @SerializedName("lang") var lang: String?,
        @SerializedName("files") var files: List<EpisodeFileData>,
        @SerializedName("subtitles") var subtitles: List<Subtitle>
) : Parcelable