package hexlay.movyeah.models.movie.helpers

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.models.movie.attributes.Language
import hexlay.movyeah.models.movie.attributes.Plot
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class LanguageHelper(
        @SerializedName("data") var data: List<Language>
) : Parcelable