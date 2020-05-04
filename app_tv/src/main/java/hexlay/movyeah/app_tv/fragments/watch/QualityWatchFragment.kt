package hexlay.movyeah.app_tv.fragments.watch

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.helpers.translateQuality
import hexlay.movyeah.app_tv.models.events.watch.WatchQualityChangeEvent
import org.greenrobot.eventbus.EventBus


class QualityWatchFragment : GuidedStepSupportFragment() {

    private var qualities = mutableListOf<String>()
    private var currentKey = "NONE"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.full_chquality), currentKey.translateQuality(requireContext()), "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        qualities.forEach {
            actions.add(
                    GuidedAction.Builder(requireActivity())
                            .title(it)
                            .build()
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        EventBus.getDefault().post(WatchQualityChangeEvent(action.title.toString()))
        parentFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance(key: String, qualities: List<String>): QualityWatchFragment {
            val fragment = QualityWatchFragment()
            fragment.qualities.addAll(qualities)
            fragment.currentKey = key
            return fragment
        }
    }

}