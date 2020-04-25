package movyeahtv.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import com.afollestad.inlineactivityresult.startActivityForResult
import hexlay.movyeah.R
import hexlay.movyeah.api.view_models.MovieListViewModel
import hexlay.movyeah.database.view_models.DbCategoryViewModel
import hexlay.movyeah.database.view_models.DbMovieViewModel
import hexlay.movyeah.helpers.Constants
import hexlay.movyeah.helpers.differsFrom
import hexlay.movyeah.helpers.getWindow
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.movie.Movie
import movyeahtv.activities.TvSearchActivity
import movyeahtv.activities.TvWatchActivity
import movyeahtv.fragments.preferences.CategoryPreferenceFragment
import movyeahtv.fragments.preferences.LanguagePreferenceFragment
import movyeahtv.fragments.preferences.SortPreferenceFragment
import movyeahtv.fragments.preferences.YearPreferenceFragment
import movyeahtv.helpers.setDrawableFromUrl
import movyeahtv.models.PreferenceModel
import movyeahtv.models.events.StartActivityEvent
import movyeahtv.models.events.filter.CategoryChangeEvent
import movyeahtv.models.events.filter.LanguageChangeEvent
import movyeahtv.models.events.filter.SortChangeEvent
import movyeahtv.models.events.filter.YearChangeEvent
import movyeahtv.presenters.MoviePresenter
import movyeahtv.presenters.PreferencePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class TvMainFragment : BrowseSupportFragment() {

    private val movieListViewModel by viewModels<MovieListViewModel>()
    private val dbMovieViewModel by viewModels<DbMovieViewModel>()
    private val dbCategories by viewModels<DbCategoryViewModel>()
    private var backgroundManager: BackgroundManager? = null

    private var mainAdapter: ArrayObjectAdapter? = null
    private var movieAdapter: ArrayObjectAdapter? = null
    private var seriesAdapter: ArrayObjectAdapter? = null
    private var favoriteAdapter: ArrayObjectAdapter? = null

    private val perPage = 20
    private var moviesPage = 1
    private var tvsPage = 1
    private var savedCover: String? = null

    // Filter attributes
    private var sortingMethod = "-upload_date"
    private var endYear = 0
    private var startYear = 0
    private var categories = ArrayList<String>()
    private var language: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startYear = Constants.START_YEAR
        endYear = Constants.END_YEAR
        initBackgroundManager()
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initAdapters()
        initRows()
        initData()
    }

    private fun initBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(requireActivity())
        if (!backgroundManager!!.isAttached)
            backgroundManager!!.attach(getWindow())
    }

    private fun initUi() {
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        searchAffordanceColor = Color.DKGRAY
        setOnSearchClickedListener {
            startActivityForResult<TvSearchActivity>(requestCode = 1) { _, _ ->
                savedCover?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
            }
        }
    }

    private fun fetchMovies() {
        movieListViewModel.fetchMovies(
                page = moviesPage,
                filtersLanguage = language,
                filtersGenres = if (categories.size > 0) {
                    categories.joinToString { it }
                } else {
                    null
                },
                filtersSort = sortingMethod,
                filtersYears = "${startYear},${endYear}"
        ).observeOnce(viewLifecycleOwner, Observer {
            handleMovies(it)
        })
    }

    private fun fetchSeries() {
        movieListViewModel.fetchMovies(
                page = tvsPage,
                filtersType = "series",
                filtersLanguage = language,
                filtersGenres = if (categories.size > 0) {
                    categories.joinToString { it }
                } else {
                    null
                },
                filtersSort = sortingMethod,
                filtersYears = "${startYear},${endYear}"
        ).observeOnce(viewLifecycleOwner, Observer {
            handleTvs(it)
        })
    }

    private fun initData() {
        fetchMovies()
        fetchSeries()
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, row ->
            when (row.id) {
                0L -> {
                    val position = movieAdapter?.indexOf(item)!!
                    val page = ((position + 1) / perPage) + 1
                    if (position > 0 && page == moviesPage) {
                        fetchMovies()
                    }
                }
                1L -> {
                    val position = seriesAdapter?.indexOf(item)!!
                    val page = ((position + 1) / perPage) + 1
                    if (position > 0 && page == tvsPage) {
                        fetchSeries()
                    }
                }
            }
            if (item is Movie) {
                savedCover = item.getCover()
                savedCover?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
            } else {
                backgroundManager?.color = ContextCompat.getColor(requireContext(), R.color.default_background2)
            }
        }
    }

    private fun initRows() {
        val movieHeader = HeaderItem(0, getString(R.string.menu_movies))
        val tvsHeader = HeaderItem(1, getString(R.string.menu_series))
        val preferenceHeader = HeaderItem(2, getString(R.string.settings_title))
        val favoriteHeader = HeaderItem(3, getString(R.string.menu_favorites))
        dbMovieViewModel.getMovies()?.observe(viewLifecycleOwner, Observer {
            favoriteAdapter?.clear()
            for (favorite in it) {
                favoriteAdapter?.add(favorite)
            }
        })
        mainAdapter?.add(ListRow(movieHeader, movieAdapter))
        mainAdapter?.add(ListRow(tvsHeader, seriesAdapter))
        mainAdapter?.add(ListRow(favoriteHeader, favoriteAdapter))
        mainAdapter?.add(ListRow(preferenceHeader, setupPreferenceAdapter()))
        adapter = mainAdapter
    }

    private fun initAdapters() {
        mainAdapter = ArrayObjectAdapter(ListRowPresenter())
        movieAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
        seriesAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
        favoriteAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
    }

    private fun setupPreferenceAdapter(): ArrayObjectAdapter {
        val preferences = arrayOf(
                PreferenceModel(
                        getString(R.string.filter_change_lang),
                        "preference_language",
                        LanguagePreferenceFragment()
                ),
                PreferenceModel(
                        getString(R.string.filter_change_category),
                        "preference_category",
                        Fragment()
                ),
                PreferenceModel(
                        getString(R.string.filter_change_year),
                        "preference_year",
                        YearPreferenceFragment.newInstance(startYear, endYear)
                ),
                PreferenceModel(
                        getString(R.string.filter_change_sort),
                        "preference_sort",
                        SortPreferenceFragment()
                ),
                PreferenceModel(
                        getString(R.string.settings_about),
                        "preference_about",
                        TvAboutFragment()
                )
        )
        val preferenceAdapter = ArrayObjectAdapter(PreferencePresenter(requireContext()))
        preferences.forEach {
            // Very bad approach. IDK any other way for GuidedFragment actions :(
            if (it.key == "preference_category") {
                dbCategories.getCategories()?.observeOnce(viewLifecycleOwner, Observer { dbCats ->
                    it.fragment = CategoryPreferenceFragment.newInstance(categories, dbCats)
                })
            }
            preferenceAdapter.add(it)
        }
        return preferenceAdapter
    }

    @Subscribe
    fun listenYearChange(event: YearChangeEvent) {
        val pStartYear = startYear
        val pEndYear = endYear
        startYear = event.startYear
        endYear = event.endYear
        if (pStartYear != startYear || pEndYear != endYear) {
            resetList()
        }
    }

    @Subscribe
    fun listenLanguageChange(event: LanguageChangeEvent) {
        val pLanguage = language
        language = if (event.language == "ALL") {
            null
        } else {
            event.language
        }
        if (pLanguage != language) {
            resetList()
        }
    }

    @Subscribe
    fun listenSortChange(event: SortChangeEvent) {
        val pSort = sortingMethod
        sortingMethod = event.sort
        if (pSort != sortingMethod) {
            resetList()
        }
    }

    @Subscribe
    fun listenCategoryChange(event: CategoryChangeEvent) {
        val pCategories = categories
        categories.clear()
        categories.addAll(event.categories)
        if (pCategories.differsFrom(categories)) {
            resetList()
        }
    }

    @Subscribe
    fun listenActivityStart(event: StartActivityEvent) {
        when (event.key) {
            "TvWatchActivity" -> {
                startActivityForResult<TvWatchActivity>(event.params, requestCode = 2) { _, _ ->
                    savedCover?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
                }
            }
        }
    }

    private fun resetList() {
        moviesPage = 1
        tvsPage = 1
        movieAdapter?.clear()
        seriesAdapter?.clear()
        fetchMovies()
        fetchSeries()
    }

    private fun handleMovies(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            movieAdapter?.addAll(movieAdapter!!.size(), dataList)
            moviesPage++
        }
    }

    private fun handleTvs(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            seriesAdapter?.addAll(seriesAdapter!!.size(), dataList)
            tvsPage++
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

}