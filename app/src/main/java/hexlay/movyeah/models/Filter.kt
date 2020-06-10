package hexlay.movyeah.models

import androidx.fragment.app.Fragment
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.differsFrom

data class Filter(
        var activeFragment: Fragment,
        var sortingMethod: String = "-upload_date",
        var startYear: Int = Constants.START_YEAR,
        var endYear: Int = Constants.END_YEAR,
        var categories: ArrayList<String> = ArrayList(),
        var countries: ArrayList<String> = ArrayList(),
        var language: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (other is Filter) {
            return other.sortingMethod != sortingMethod
                    || other.endYear != endYear
                    || other.startYear != startYear
                    || other.language != language
                    || categories.differsFrom(other.categories)
                    || countries.differsFrom(other.countries)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = activeFragment.hashCode()
        result = 31 * result + sortingMethod.hashCode()
        result = 31 * result + startYear
        result = 31 * result + endYear
        result = 31 * result + categories.hashCode()
        result = 31 * result + countries.hashCode()
        result = 31 * result + (language?.hashCode() ?: 0)
        return result
    }

}