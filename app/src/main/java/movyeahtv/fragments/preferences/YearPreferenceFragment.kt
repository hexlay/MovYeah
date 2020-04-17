package movyeahtv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R
import hexlay.movyeah.helpers.Constants
import movyeahtv.models.events.filter.YearChangeEvent
import org.greenrobot.eventbus.EventBus


class YearPreferenceFragment : GuidedStepSupportFragment() {

    private var yearMode = 0
    private var startYear = 0
    private var endYear = 0

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_year), "", "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val yearList = listOf(Constants.START_YEAR..Constants.END_YEAR).flatten().map {
            GuidedAction.Builder(requireActivity())
                    .id(it.toLong())
                    .title(it.toString())
                    .build()
        }
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title(startYear.toString())
                        .subActions(yearList)
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(2L)
                        .title(endYear.toString())
                        .subActions(yearList.reversed())
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(3L)
                        .title(getString(R.string.done))
                        .build()
        )
    }

    override fun onSubGuidedActionClicked(action: GuidedAction): Boolean {
        if (yearMode == 1) {
            startYear = action.id.toInt()
            actions[0].title = startYear.toString()
            notifyActionChanged(0)
        } else {
            endYear = action.id.toInt()
            actions[1].title = endYear.toString()
            notifyActionChanged(1)
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
        fun newInstance(startYear: Int, endYear: Int): YearPreferenceFragment {
            val fragment = YearPreferenceFragment()
            fragment.startYear = startYear
            fragment.endYear = endYear
            return fragment
        }
    }

}