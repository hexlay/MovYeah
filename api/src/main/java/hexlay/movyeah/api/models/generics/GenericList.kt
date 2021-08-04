package hexlay.movyeah.api.models.generics

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GenericList<T : Parcelable>(
        @SerializedName("data") var data: List<T>
)