package hexlay.movyeah.app_tv.fragments.watch

import android.os.Bundle
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.util.putAll
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.api.models.attributes.show.Episode
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.models.events.watch.WatchEpisodeChangeEvent
import org.greenrobot.eventbus.EventBus


class EpisodeWatchFragment : GuidedStepSupportFragment() {

    private var tvShowSeasons: SparseArray<List<Episode>> = SparseArray()
    private var currentSeason = 1
    private var currentEpisode = 1

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_episode), "S${currentSeason}E${currentEpisode + 1}", "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        tvShowSeasons.forEach { key, value ->
            val episodes = mutableListOf<GuidedAction>()
            value.forEach {
                episodes.add(
                        GuidedAction.Builder(requireActivity())
                                .id(it.episode.toLong())
                                .title(it.getEpisodeTitle())
                                .build()
                )
            }
            actions.add(
                    GuidedAction.Builder(requireActivity())
                            .id(key.toLong())
                            .title("სეზონი $key")
                            .subActions(episodes)
                            .build()
            )
        }
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        currentEpisode = action.id.toInt() - 1
        EventBus.getDefault().post(WatchEpisodeChangeEvent(currentSeason, currentEpisode))
        parentFragmentManager.popBackStack()
        return true
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        currentSeason = action.id.toInt()
    }

    companion object {
        fun newInstance(season: Int, episode: Int, tvShowSeasons: SparseArray<List<Episode>>): EpisodeWatchFragment {
            val fragment = EpisodeWatchFragment()
            fragment.tvShowSeasons.putAll(tvShowSeasons)
            fragment.currentSeason = season
            fragment.currentEpisode = episode
            return fragment
        }
    }

}