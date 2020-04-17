package movyeahtv.fragments.watch

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R
import hexlay.movyeah.helpers.translateLanguage
import hexlay.movyeah.models.movie.attributes.Subtitle
import movyeahtv.models.events.watch.WatchSubtitleChangeEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class SubtitleWatchFragment : GuidedStepSupportFragment() {

    private var subtitles = mutableListOf<Subtitle>()
    private var currentKey = "NONE"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_lang), currentKey.translateLanguage(requireContext()), "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        subtitles.forEach {
            actions.add(
                    GuidedAction.Builder(requireActivity())
                            .title(it.lang?.toUpperCase(Locale.ENGLISH)!!)
                            .build()
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        EventBus.getDefault().post(WatchSubtitleChangeEvent(action.title.toString()))
        parentFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance(key: String, subtitles: List<Subtitle>): SubtitleWatchFragment {
            val fragment = SubtitleWatchFragment()
            fragment.subtitles.addAll(subtitles)
            fragment.currentKey = key
            return fragment
        }
    }

}