package hexlay.movyeah.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import hexlay.movyeah.fragments.SeasonFragment
import hexlay.movyeah.models.movie.attributes.show.Episode

class SeasonPageAdapter(
        fragmentManager: FragmentManager,
        private val movieId : Int,
        private val seasons: SparseArray<List<Episode>>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItemPosition(`object`: Any): Int = POSITION_NONE

    override fun getItem(position: Int): Fragment {
        val season = position + 1
        return SeasonFragment.newInstance(season, movieId, seasons[season]!!)
    }

    override fun getPageTitle(position: Int): CharSequence? = "სეზონი ${position + 1}"

    override fun getCount(): Int = seasons.size()

}
