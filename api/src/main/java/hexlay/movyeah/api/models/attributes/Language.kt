package hexlay.movyeah.api.models.attributes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(
        @SerializedName("code") var code: String?,
        @SerializedName("primaryName") var primaryName: String?
) : Parcelable