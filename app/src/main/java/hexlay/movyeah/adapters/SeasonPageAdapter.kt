package hexlay.movyeah.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.fragments.SeasonFragment

class SeasonPageAdapter(
        fragmentManager: FragmentManager,
        private val movie: Movie,
        private val seasons: SparseArray<List<Episode>>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    override fun getItem(position: Int): Fragment {
        val season = position + 1
        return SeasonFragment.newInstance(season, movie, seasons[season]!!)
    }

    override fun getPageTitle(position: Int): CharSequence = "სეზონი ${position + 1}"

    override fun getCount(): Int = seasons.size()

}
