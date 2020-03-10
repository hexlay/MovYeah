package hexlay.movyeah.models.movie.attributes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Actor(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("originalName") var originalName: String?,
        @SerializedName("primaryName") var primaryName: String?,
        @SerializedName("poster") var poster: String?
) : Parcelable {

        fun getTitle(): String? {
                return if (primaryName?.isEmpty()!!) {
                        originalName
                } else {
                        primaryName
                }
        }

}
