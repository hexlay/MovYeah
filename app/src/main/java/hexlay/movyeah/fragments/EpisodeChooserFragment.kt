package hexlay.movyeah.fragments

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import hexlay.movyeah.R
import hexlay.movyeah.adapters.SeasonPageAdapter
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.models.attributes.show.Episode
import kotlinx.android.synthetic.main.fragment_episode_chooser.*

class EpisodeChooserFragment : BottomSheetDialogFragment() {

    private var movie: Movie? = null
    private var tvShowSeasons: SparseArray<List<Episode>> = SparseArray()
    private var currentSeason = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        episode_holder.adapter = SeasonPageAdapter(requireActivity(), movie!!, tvShowSeasons)
        TabLayoutMediator(season_tabs, episode_holder) { tab, position ->
            tab.text = "სეზონი ${position + 1}"
        }.attach()
        episode_holder.currentItem = currentSeason - 1
        episode_holder.setPageTransformer { page, position ->
            if (position == 0.0f) {
                page.isNestedScrollingEnabled = true
            } else if (position % 1 == 0.0f) {
                page.isNestedScrollingEnabled = false
            }
        }
    }

    companion object {

        fun newInstance(movie: Movie, tvShowSeasons: SparseArray<List<Episode>>, currentSeason: Int): EpisodeChooserFragment {
            val episodeChooserFragment = EpisodeChooserFragment()
            episodeChooserFragment.movie = movie
            episodeChooserFragment.tvShowSeasons = tvShowSeasons
            episodeChooserFragment.currentSeason = currentSeason
            return episodeChooserFragment
        }

    }

}