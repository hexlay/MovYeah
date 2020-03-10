package hexlay.movyeah.models.movie.attributes.show

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EpisodeFileData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("quality") var quality: String?,
        @SerializedName("src") var src: String?
) : Parcelable