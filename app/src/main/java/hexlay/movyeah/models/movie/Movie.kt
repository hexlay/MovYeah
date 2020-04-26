package hexlay.movyeah.models.movie

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import hexlay.movyeah.helpers.toCommaList
import hexlay.movyeah.models.movie.attributes.Rating
import hexlay.movyeah.models.movie.generics.GenericMap
import hexlay.movyeah.models.movie.helpers.CategoryHelper
import hexlay.movyeah.models.movie.helpers.LanguageHelper
import hexlay.movyeah.models.movie.helpers.PlotHelper
import hexlay.movyeah.models.movie.helpers.SeasonHelper
import kotlinx.android.parcel.Parcelize
import kotlin.math.ln
import kotlin.math.pow

@Parcelize
@Entity
data class Movie(
        @PrimaryKey @SerializedName("id") var id: Int = 0,
        @SerializedName("adjaraId") var adjaraId: Int = 0,
        @SerializedName("duration") var duration: Int = 0,
        @SerializedName("year") var year: Int = 0,
        @SerializedName("watchCount") var watchCount: Int = 0,
        @SerializedName("isTvShow") var isTvShow: Boolean = false,
        @SerializedName("primaryName") var primaryName: String?,
        @SerializedName("originalName") var originalName: String?,
        @SerializedName("secondaryName") var secondaryName: String?,
        @SerializedName("imdbUrl") var imdbUrl: String?,
        @SerializedName("poster") var poster: String?,
        @Embedded(prefix = "posters_") @SerializedName("posters") var posters: GenericMap?,
        @Embedded(prefix = "covers_") @SerializedName("covers") var covers: GenericMap?,
        @SerializedName("rating") var rating: Map<String, Rating>?,
        @Embedded(prefix = "plots_") @SerializedName("plots") var plots: PlotHelper?,
        @Embedded(prefix = "languages_") @SerializedName("languages") var languages: LanguageHelper?,
        @Embedded(prefix = "genres_") @SerializedName("genres") var genres: CategoryHelper?,
        @Embedded(prefix = "seasons_") @SerializedName("seasons") var seasons: SeasonHelper?
) : Parcelable {

    fun getRealId(): Int {
        return if (isTvShow) adjaraId else id
    }

    fun getTitle(): String {
        return when {
            primaryName?.isNotEmpty()!! -> {
                primaryName
            }
            originalName?.isNotEmpty()!! -> {
                originalName
            }
            secondaryName?.isNotEmpty()!! -> {
                secondaryName
            }
            else -> "სათაური ვერ მოიძებნა"
        } ?: "სათაური ვერ მოიძებნა"
    }

    fun getTruePoster(): String? = posters?.data?.asSequence()?.firstOrNull { it.value.isNotEmpty() }?.value

    fun getDescription(): String = plots?.data?.firstOrNull { it.description?.isNotEmpty()!! }?.description ?: "აღწერა ვერ მოიძებნა"

    fun getRating(type: String): Double = rating?.get(type)?.score ?: Rating(0.0, 0).score

    fun getCover(): String? = covers?.data?.asSequence()?.firstOrNull { it.value.isNotEmpty() }?.value

    fun getGenresString(): String {
        val genres = genres?.data?.map { it.primaryName }?.toCommaList()
        return genres ?: "კატეგორიები ვერ მოიძებნა"
    }

    fun getWatchString(): String {
        if (watchCount < 1000)
            return watchCount.toString()
        val exp = (ln(watchCount.toDouble()) / ln(1000.0)).toInt()
        return String.format("%.1f %c", watchCount / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
    }

    override fun equals(other: Any?): Boolean {
        if (other is Movie) {
            return id == other.id || adjaraId == other.adjaraId
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + adjaraId
        result = 31 * result + duration
        result = 31 * result + year
        result = 31 * result + watchCount
        result = 31 * result + isTvShow.hashCode()
        result = 31 * result + (primaryName?.hashCode() ?: 0)
        result = 31 * result + (originalName?.hashCode() ?: 0)
        result = 31 * result + (secondaryName?.hashCode() ?: 0)
        result = 31 * result + (imdbUrl?.hashCode() ?: 0)
        result = 31 * result + (poster?.hashCode() ?: 0)
        result = 31 * result + (posters?.hashCode() ?: 0)
        result = 31 * result + (covers?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (plots?.hashCode() ?: 0)
        result = 31 * result + (languages?.hashCode() ?: 0)
        result = 31 * result + (genres?.hashCode() ?: 0)
        result = 31 * result + (seasons?.hashCode() ?: 0)
        return result
    }

}