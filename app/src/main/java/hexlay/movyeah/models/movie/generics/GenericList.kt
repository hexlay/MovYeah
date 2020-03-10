package hexlay.movyeah.models.movie.generics

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenericList<T : Parcelable>(
        @SerializedName("data") var data: List<T>
) : Parcelable