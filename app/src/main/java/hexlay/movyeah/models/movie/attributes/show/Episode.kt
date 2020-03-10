package hexlay.movyeah.models.movie.attributes.show

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
        @SerializedName("episode") var episode: Int = 0,
        @SerializedName("title") var title: String?,
        @SerializedName("files") var files: List<EpisodeFile>
) : Parcelable {

    fun getEpisodeTitle(): String {
        return (if (title?.isNotEmpty()!!)
            title
        else "სათაური ვერ მოიძებნა") ?: "სათაური ვერ მოიძებნა"
    }

}