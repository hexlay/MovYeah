package hexlay.movyeah.api.models.helpers

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.api.models.attributes.Season
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class SeasonHelper(
        @SerializedName("data") var data: List<Season>
) : Parcelable