package hexlay.movyeah.models.movie.helpers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.models.movie.Movie
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtendedMovieHelper(
        @SerializedName("data") var data: Movie
) : Parcelable