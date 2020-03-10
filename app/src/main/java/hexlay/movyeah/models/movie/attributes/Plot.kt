package hexlay.movyeah.models.movie.attributes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Plot(
        @SerializedName("description") var description: String?,
        @SerializedName("language") var language: String?
) : Parcelable