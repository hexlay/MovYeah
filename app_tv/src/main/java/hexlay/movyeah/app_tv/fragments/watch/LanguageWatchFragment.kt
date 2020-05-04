package hexlay.movyeah.app_tv.fragments.watch

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.app_tv.R
import hexlay.movyeah.app_tv.helpers.translateLanguage
import hexlay.movyeah.app_tv.models.events.watch.WatchLanguageChangeEvent
import org.greenrobot.eventbus.EventBus


class LanguageWatchFragment : GuidedStepSupportFragment() {

    private var languages = mutableListOf<String>()
    private var currentKey = "NONE"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_lang), currentKey.translateLanguage(requireContext()), "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        languages.forEach {
            actions.add(
                    GuidedAction.Builder(requireActivity())
                            .title(it)
                            .build()
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        EventBus.getDefault().post(WatchLanguageChangeEvent(action.title.toString()))
        parentFragmentManager.popBackStack()
    }

    companion object {
        fun newInstance(key: String, languages: List<String>): LanguageWatchFragment {
            val fragment = LanguageWatchFragment()
            fragment.languages.addAll(languages)
            fragment.currentKey = key
            return fragment
        }
    }

}