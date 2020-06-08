package hexlay.movyeah.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.MovieListViewModel
import hexlay.movyeah.fragments.FilterFragment
import hexlay.movyeah.helpers.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.piece_scroll_up.*

abstract class AbsMoviesFragment : Fragment() {

    protected val movieListViewModel by viewModels<MovieListViewModel>()
    protected var filter: FilterFragment? = null
    protected var page = 1
    private var loading = true

    // Filter attributes
    var sortingMethod = "-upload_date"
    var endYear = 0
    var startYear = 0
    var categories = ArrayList<String>()
    var countries = ArrayList<String>()
    var language: String? = null

    private val source = emptyDataSourceTyped<Movie>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initReloader() {
        movies_reloader.setProgressViewOffset(false, 0, getStatusBarHeight() + getActionBarSize() + dpOf(5))
        movies_reloader.setOnRefreshListener {
            zeroLoadMovies()
            movies_reloader.isRefreshing = false
        }
    }

    protected open fun initFragment() {
        startYear = Constants.START_YEAR
        endYear = Constants.END_YEAR
        initReloader()
        initRecyclerView()
        initFilter()
        initScrollUp()
        zeroLoadMovies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    protected fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        val recyclerPaddingTop = getStatusBarHeight() + getActionBarSize() + dpOf(10)
        movies_holder.setPadding(0, recyclerPaddingTop, 0, getActionBarSize())
        movies_holder.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var pastVisibleItems = 0
            var visibleItemCount = 0
            var totalItemCount = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (source.isNotEmpty()) {
                    if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        scroll_up.hide()
                        fab_filter.extend()
                    } else {
                        scroll_up.show()
                        fab_filter.shrink()
                    }
                }
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.childCount
                    totalItemCount = gridLayoutManager.itemCount
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount - 10) {
                            loading = false
                            loadMovies()
                        }
                    }
                }
            }
        })
        movies_holder.setup {
            withDataSource(source)
            withLayoutManager(gridLayoutManager)
            withItem<Movie, MovieViewHolder>(R.layout.list_items_extended) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, requireActivity())
                }
            }
        }
    }

    protected open fun initFilter() {
        fab_filter.setMargins(bottom = getActionBarSize() + dpOf(15))
    }

    protected open fun initScrollUp() {
        var scrolling = false
        scroll_up.setMargins(bottom = getActionBarSize() + dpOf(20))
        scroll_up.setOnClickListener {
            scrolling = if (!scrolling) {
                movies_holder.smoothScrollToPosition(0)
                true
            } else {
                movies_holder.scrollToPosition(0)
                false
            }
        }
    }

    abstract fun loadMovies()

    protected fun zeroLoadMovies() {
        page = 1
        loading_movies.isGone = false
        fab_filter.hide()
        loadMovies()
    }

    fun filter(filterSort: String, filterEndYear: Int, filterStartYear: Int, filterLanguage: String?, filterCategories: ArrayList<String>, filterCountries: ArrayList<String>): Boolean {
        val filtered = isDifference(filterSort, filterEndYear, filterStartYear, filterLanguage, filterCategories, filterCountries)
        if (filtered) {
            sortingMethod = filterSort
            endYear = filterEndYear
            startYear = filterStartYear
            categories.clear()
            categories.addAll(filterCategories)
            countries.clear()
            countries.addAll(filterCountries)
            language = filterLanguage
            zeroLoadMovies()
        }
        return filtered
    }

    private fun isDifference(filterSort: String, filterEndYear: Int, filterStartYear: Int, filterLanguage: String?, filterCategories: ArrayList<String>, filterCountries: ArrayList<String>): Boolean {
        return filterSort != sortingMethod
                || filterEndYear != endYear
                || filterStartYear != startYear
                || filterLanguage != language
                || categories.differsFrom(filterCategories)
                || countries.differsFrom(filterCountries)
    }

    protected open fun handleMovies(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            warning_holder.isGone = true
            if (page > 1) {
                source.addAll(dataList)
                loading = true
            } else {
                source.clear()
                source.addAll(dataList)
                loading_movies.isGone = true
                fab_filter.show()
            }
            page++
        } else {
            if (page == 1) {
                warning_holder.text = getString(R.string.loading_news_fail)
                warning_holder.isVisible = true
                loading_movies.isGone = true
                source.clear()
                fab_filter.show()
            }
        }
    }

}