package movyeahtv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R
import hexlay.movyeah.helpers.translateLanguage
import movyeahtv.models.events.LanguageChangeEvent
import org.greenrobot.eventbus.EventBus


class LanguagePreferenceFragment : GuidedStepSupportFragment() {

    private var title: String? = null
    private var language = "ALL"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(title, language.translateLanguage(requireContext()), "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title("ყველა")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(2L)
                        .title(getString(R.string.full_geo))
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(3L)
                        .title(getString(R.string.full_eng))
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(4L)
                        .title(getString(R.string.full_rus))
                        .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        language = when(action.id) {
            2L -> "GEO"
            3L -> "ENG"
            4L -> "RUS"
            else -> "ALL"
        }
        EventBus.getDefault().post(LanguageChangeEvent(language))
        parentFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance(title: String): LanguagePreferenceFragment {
            val fragment = LanguagePreferenceFragment()
            fragment.title = title
            return fragment
        }
    }

}