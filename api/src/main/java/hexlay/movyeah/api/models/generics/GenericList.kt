package hexlay.movyeah.api.models.generics

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GenericList<T : Parcelable>(
        @SerializedName("data") var data: List<T>
) : Parcelable