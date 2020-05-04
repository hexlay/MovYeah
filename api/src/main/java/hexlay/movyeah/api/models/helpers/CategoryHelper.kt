package hexlay.movyeah.api.models.helpers

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.api.models.attributes.Category
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class CategoryHelper(
        @SerializedName("data") var data: List<Category>
) : Parcelable