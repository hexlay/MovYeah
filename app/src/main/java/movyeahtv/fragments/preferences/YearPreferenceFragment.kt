package movyeahtv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R
import movyeahtv.models.events.YearChangeEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class YearPreferenceFragment : GuidedStepSupportFragment() {

    private var title: String? = null
    private var yearMode = 0
    private var startYear = 0
    private var endYear = 0

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.app_name), "", "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val yearList = listOf(1900..Calendar.getInstance().get(Calendar.YEAR)).flatten().map {
            GuidedAction.Builder(requireActivity())
                    .id(it.toLong())
                    .title(it.toString())
                    .build()
        }
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title("დასაწყისი")
                        .subActions(yearList)
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(2L)
                        .title("დასასრული")
                        .subActions(yearList.reversed())
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(3L)
                        .title("დადასტურება")
                        .build()
        )
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        if (yearMode == 1) {
            startYear = action.id.toInt()
        } else {
            endYear = action.id.toInt()
        }
        return true
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == 3L) {
            EventBus.getDefault().post(YearChangeEvent(startYear, endYear))
            parentFragmentManager.popBackStack()
        } else {
            yearMode = action.id.toInt()
        }
    }

    companion object {
        fun newInstance(title: String): YearPreferenceFragment {
            val fragment = YearPreferenceFragment()
            fragment.title = title
            return fragment
        }
    }

}