package hexlay.movyeah.api.models.helpers

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.api.models.attributes.Plot
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class PlotHelper(
        @SerializedName("data") var data: List<Plot>
) : Parcelable