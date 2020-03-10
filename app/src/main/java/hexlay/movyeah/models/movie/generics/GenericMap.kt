package hexlay.movyeah.models.movie.generics

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class GenericMap(
        @SerializedName("data") var data: Map<String, String>
) : Parcelable