package movyeahtv.fragments.preferences

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import hexlay.movyeah.R
import hexlay.movyeah.models.movie.attributes.Category
import movyeahtv.models.events.filter.CategoryChangeEvent
import org.greenrobot.eventbus.EventBus

class CategoryPreferenceFragment : GuidedStepSupportFragment() {

    private var categoriesList = ArrayList<Category>()
    private var categories = ArrayList<String>()

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(getString(R.string.filter_change_category), "", "", null)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        for (category in categoriesList) {
            val checked = categories.contains(category.id.toString())
            actions.add(
                    GuidedAction.Builder(requireActivity())
                            .id(category.id.toLong())
                            .title(category.primaryName)
                            .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                            .checked(checked)
                            .build()
            )
        }
        actions.add(
                GuidedAction.Builder(requireActivity())
                        .id(1L)
                        .title(getString(R.string.done))
                        .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == 1L) {
            EventBus.getDefault().post(CategoryChangeEvent(categories))
            parentFragmentManager.popBackStack()
        } else {
            val id = action.id.toString()
            if (action.isChecked) {
                categories.add(id)
            } else {
                categories.remove(id)
            }
        }
    }

    companion object {
        fun newInstance(currentCats: ArrayList<String>, allCats: List<Category>): CategoryPreferenceFragment {
            val fragment = CategoryPreferenceFragment()
            fragment.categories.addAll(currentCats)
            fragment.categoriesList.addAll(allCats)
            return fragment
        }
    }

}