package movyeahtv.fragments

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R

class TvAboutFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(getString(R.string.app_name), getString(R.string.about_resources_tv), getString(R.string.about_developer), null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("Adjaranet")
                        .description("Adjaranet's team")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("Retrofit")
                        .description("Square")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("OkHttp")
                        .description("Square")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("Glide")
                        .description("Markus Junginger")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("ExoPlayer 2")
                        .description("Google")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("Android Room")
                        .description("Google")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("Inline Activity Result")
                        .description("Aidan Follestad")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .title("EventBus")
                        .description("Markus Junginger")
                        .build()
        )
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title(getString(R.string.back))
                        .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            1L -> {
                parentFragmentManager.popBackStack()
            }
        }
    }

}