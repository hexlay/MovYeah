package hexlay.movyeah.app_tv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.models.events.filter.SortChangeEvent
import org.greenrobot.eventbus.EventBus


class SortPreferenceFragment : GuidedStepSupportFragment() {

    private var sort = "-upload_date"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_sort), "", "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title("დამატების თარიღი")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(2L)
                        .title("IMDB რეიტინგი")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(3L)
                        .title("გამოშვების წელი")
                        .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        sort = when(action.id) {
            2L -> "-imdb_rating"
            3L -> "-year"
            else -> "-upload_date"
        }
        EventBus.getDefault().post(SortChangeEvent(sort))
        parentFragmentManager.popBackStack()
    }

}