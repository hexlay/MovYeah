package movyeahtv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import movyeahtv.models.events.SortChangeEvent
import org.greenrobot.eventbus.EventBus


class SortPreferenceFragment : GuidedStepSupportFragment() {

    private var title: String? = null
    private var sort = "-upload_date"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(title, "", "", null)
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

    companion object {
        fun newInstance(title: String): SortPreferenceFragment {
            val fragment = SortPreferenceFragment()
            fragment.title = title
            return fragment
        }
    }

}