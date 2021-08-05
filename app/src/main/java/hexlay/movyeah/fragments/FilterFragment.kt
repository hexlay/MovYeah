package hexlay.movyeah.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hexlay.movyeah.R
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.database.view_models.DbCountryViewModel
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.Filter
import kotlinx.android.synthetic.main.fragment_filter.*
import org.greenrobot.eventbus.EventBus

@SuppressLint("CheckResult")
class FilterFragment : BottomSheetDialogFragment() {

    private val dbCountries by viewModels<DbCountryViewModel>()
    private val dbCategories by viewModels<DbCategoryViewModel>()

    private lateinit var filter: Filter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupFilterMethods()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        EventBus.getDefault().post(filter)
        super.onDismiss(dialog)
    }

    private fun setupYears() {
        // Start year
        val yearList = listOf(Constants.START_YEAR..Constants.END_YEAR).flatten().map { it.toString() }
        start_year.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_year)
                listItemsSingleChoice(items = yearList, initialSelection = yearList.indexOf(filter.startYear.toString())) { _, index, _ ->
                    filter.startYear = yearList[index].toInt()
                }
                positiveButton(R.string.choose)
                negativeButton(R.string.cancel)
            }
        }

        // End year
        val endYears = yearList.reversed()
        end_year.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_year)
                listItemsSingleChoice(items = endYears, initialSelection = endYears.indexOf(filter.endYear.toString())) { _, index, _ ->
                    filter.endYear = endYears[index].toInt()
                }
                positiveButton(R.string.choose)
                negativeButton(R.string.cancel)
            }
        }
    }

    private fun setupSorter() {
        val items = listOf("დამატების თარიღი", "IMDB რეიტინგი", "გამოშვების წელი")
        val itemValues = listOf("-upload_date", "-imdb_rating", "-year")
        sort_changer.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_sort)
                listItemsSingleChoice(items = items, initialSelection = itemValues.indexOf(filter.sortingMethod)) { _, index, _ ->
                    filter.sortingMethod = itemValues[index]
                }
                positiveButton(R.string.choose)
                negativeButton(R.string.cancel)
            }
        }
    }

    private fun setupLanguageChanger() {
        val items = listOf("ყველა", "ქართულად", "ინგლისურად", "რუსულად")
        val itemValues = listOf(null, "GEO", "ENG", "RUS")
        language_changer.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_lang)
                listItemsSingleChoice(items = items, initialSelection = itemValues.indexOf(filter.language)) { _, index, _ ->
                    filter.language = itemValues[index]
                }
                positiveButton(R.string.choose)
                negativeButton(R.string.cancel)
            }
        }
    }

    private fun setupFilterMethods() {
        setupYears()
        setupCategories()
        setupCountries()
        setupSorter()
        setupLanguageChanger()
    }

    private fun setupCategories() {
        dbCategories.getCategories()?.observeOnce(viewLifecycleOwner, { categories ->
            val list = mutableListOf<Int>()
            for (cat in filter.categories) {
                list.add(categories.map { it.id.toString() }.indexOf(cat))
            }
            category_changer.setOnClickListener {
                MaterialDialog(requireContext()).show {
                    title(R.string.filter_change_category)
                    listItemsMultiChoice(items = categories.map { it.primaryName!! }, initialSelection = list.toIntArray(), allowEmptySelection = true) { _, indexes, _ ->
                        filter.categories.clear()
                        for (index in indexes) {
                            filter.categories.add(categories[index].id.toString())
                        }
                    }
                    positiveButton(R.string.choose)
                    negativeButton(R.string.cancel)
                }
            }
        })
    }

    private fun setupCountries() {
        dbCountries.getCountries()?.observeOnce(viewLifecycleOwner, { countries ->
            val list = mutableListOf<Int>()
            for (cat in filter.countries) {
                list.add(countries.map { it.id.toString() }.indexOf(cat))
            }
            country_changer.setOnClickListener {
                MaterialDialog(requireContext()).show {
                    title(R.string.filter_change_country)
                    listItemsMultiChoice(items = countries.map { it.primaryName!! }, initialSelection = list.toIntArray(), allowEmptySelection = true) { _, indexes, _ ->
                        filter.countries.clear()
                        for (index in indexes) {
                            filter.countries.add(countries[index].id.toString())
                        }
                    }
                    positiveButton(R.string.choose)
                    negativeButton(R.string.cancel)
                }
            }
        })
    }

    companion object {

        fun newInstance(filter: Filter): FilterFragment {
            val filterFragment = FilterFragment()
            filterFragment.filter = Filter(
                    activeFragment = filter.activeFragment,
                    sortingMethod = filter.sortingMethod,
                    startYear = filter.startYear,
                    endYear = filter.endYear,
                    categories = ArrayList(filter.categories),
                    countries = ArrayList(filter.countries),
                    language = filter.language
            )
            return filterFragment
        }

    }

}