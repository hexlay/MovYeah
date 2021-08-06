package hexlay.movyeah.adapters

import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.fragments.SeasonFragment

class SeasonPageAdapter(
        activity: AppCompatActivity,
        private val movie: Movie,
        private val seasons: SparseArray<List<Episode>>
) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        val season = position + 1
        return SeasonFragment.newInstance(season, movie, seasons[season]!!)
    }

    override fun getItemCount(): Int = seasons.size()

}
