package hexlay.movyeah.api.models.attributes

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Rating(
        @SerializedName("score") var score: Double = 0.0,
        @SerializedName("voters") var voters: Int = 0
) : Parcelable