package hexlay.movyeah.models.movie.attributes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subtitle(
        @SerializedName("lang") var lang: String?,
        @SerializedName("url") var url: String?
) : Parcelable