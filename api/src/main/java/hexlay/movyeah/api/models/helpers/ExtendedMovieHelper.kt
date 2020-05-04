package hexlay.movyeah.api.models.helpers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.api.models.Movie
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtendedMovieHelper(
        @SerializedName("data") var data: Movie
) : Parcelable