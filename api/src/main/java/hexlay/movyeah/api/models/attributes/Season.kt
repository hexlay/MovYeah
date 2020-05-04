package hexlay.movyeah.api.models.attributes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Season(
        @SerializedName("movieId") var movieId: Int = 0,
        @SerializedName("number") var number: Int = 0,
        @SerializedName("name") var name: String?,
        @SerializedName("episodesCount") var episodesCount: Int = 0
) : Parcelable