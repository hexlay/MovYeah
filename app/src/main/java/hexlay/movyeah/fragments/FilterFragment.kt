package hexlay.movyeah.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.CategoryViewHolder
import hexlay.movyeah.adapters.view_holders.CountryViewHolder
import hexlay.movyeah.api.database.view_models.DbCategoryViewModel
import hexlay.movyeah.api.database.view_models.DbCountryViewModel
import hexlay.movyeah.api.models.attributes.Category
import hexlay.movyeah.api.models.attributes.Country
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.Filter
import kotlinx.android.synthetic.main.fragment_filter.*
import org.greenrobot.eventbus.EventBus

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
        start_year.text = filter.startYear.toString()
        start_year.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_year)
                listItems(items = yearList) { _, index, _ ->
                    filter.startYear = yearList[index].toInt()
                    setStartYear(yearList[index])
                }
            }
        }

        // End year
        val endYears = yearList.reversed()
        end_year.text = filter.endYear.toString()
        end_year.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_year)
                listItems(items = endYears) { _, index, _ ->
                    filter.endYear = endYears[index].toInt()
                    setEndYear(endYears[index])
                }
            }
        }
    }

    private fun setupSorter() {
        val items = listOf("დამატების თარიღი", "IMDB რეიტინგი", "გამოშვების წელი")
        val itemValues = listOf("-upload_date", "-imdb_rating", "-year")
        sort_changer.text = items[itemValues.indexOf(filter.sortingMethod)]
        sort_changer.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_sort)
                listItems(items = items) { _, index, _ ->
                    filter.sortingMethod = itemValues[index]
                    setSorter(items[index])
                }
            }
        }
    }

    private fun setupLanguageChanger() {
        val items = listOf("ყველა", "ქართულად", "ინგლისურად", "რუსულად")
        val itemValues = listOf(null, "GEO", "ENG", "RUS")
        language_changer.text = items[itemValues.indexOf(filter.language)]
        language_changer.setOnClickListener {
            MaterialDialog(requireContext()).show {
                title(R.string.filter_change_lang)
                listItems(items = items) { _, index, _ ->
                    filter.language = itemValues[index]
                    setLanguage(items[index])
                }
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
        val source = emptyDataSource()
        dbCategories.getCategories()?.observeOnce(viewLifecycleOwner, Observer {
            source.addAll(it)
        })
        category_holder.setup {
            withLayoutManager(GridLayoutManager(context, 2))
            withDataSource(source)
            withItem<Category, CategoryViewHolder>(R.layout.list_categories) {
                onBind(::CategoryViewHolder) { _, item ->
                    setIsRecyclable(false)
                    val categoryId = item.id.toString()
                    toggleButton.text = item.primaryName
                    toggleButton.textOff = item.primaryName
                    toggleButton.textOn = item.primaryName
                    toggleButton.isChecked = filter.categories.contains(categoryId)
                    toggleButton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            filter.categories.add(categoryId)
                        } else {
                            filter.categories.remove(categoryId)
                        }
                    }
                }
            }
        }
    }

    private fun setupCountries() {
        val source = emptyDataSource()
        dbCountries.getCountries()?.observeOnce(viewLifecycleOwner, Observer {
            source.addAll(it)
        })
        country_holder.setup {
            withLayoutManager(LinearLayoutManager(context, RecyclerView.HORIZONTAL, false))
            withDataSource(source)
            withItem<Country, CountryViewHolder>(R.layout.list_countries) {
                onBind(::CountryViewHolder) { _, item ->
                    setIsRecyclable(false)
                    val countryId = item.id.toString()
                    toggleButton.text = item.primaryName
                    toggleButton.textOff = item.primaryName
                    toggleButton.textOn = item.primaryName
                    toggleButton.isChecked = filter.countries.contains(countryId)
                    toggleButton.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            filter.countries.add(countryId)
                        } else {
                            filter.countries.remove(countryId)
                        }
                    }
                }
            }
        }
    }

    private fun setEndYear(value: String) {
        end_year.text = value
    }

    private fun setSorter(value: String) {
        sort_changer.text = value
    }

    private fun setLanguage(value: String) {
        language_changer.text = value
    }

    private fun setStartYear(value: String) {
        start_year.text = value
    }

    companion object {

        fun newInstance(filter: Filter): FilterFragment {
            val filterFragment = FilterFragment()
            filterFragment.filter = filter.copy()
            return filterFragment
        }

    }

}