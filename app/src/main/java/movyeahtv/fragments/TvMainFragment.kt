package movyeahtv.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import hexlay.movyeah.R
import hexlay.movyeah.api.view_models.MovieListViewModel
import hexlay.movyeah.database.view_models.DbMovieViewModel
import hexlay.movyeah.helpers.getWindow
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.helpers.setDrawableFromUrl
import hexlay.movyeah.models.movie.Movie
import movyeahtv.models.PreferenceModel
import movyeahtv.presenters.MoviePresenter
import movyeahtv.presenters.PreferencePresenter
import org.jetbrains.anko.support.v4.toast
import java.util.*
import kotlin.collections.ArrayList


class TvMainFragment : BrowseSupportFragment() {

    private val movieListViewModel by viewModels<MovieListViewModel>()
    private val dbMovieViewModel by viewModels<DbMovieViewModel>()
    private var mainPosition = 0
    private var mainRow = 0
    private var backgroundManager: BackgroundManager? = null

    private var mainAdapter: ArrayObjectAdapter? = null
    private var movieAdapter: ArrayObjectAdapter? = null
    private var tvShowAdapter: ArrayObjectAdapter? = null
    private var favoriteAdapter: ArrayObjectAdapter? = null

    private var moviesLoadOffset = 0
    private var tvsLoadOffset = 0
    private var moviesPage = 1
    private var tvsPage = 1

    // Filter attributes
    var sortingMethod = "-upload_date"
    var endYear = 0
    var startYear = 0
    var categories = ArrayList<String>()
    var language: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startYear = 1900
        endYear = Calendar.getInstance().get(Calendar.YEAR)
        backgroundManager = BackgroundManager.getInstance(requireActivity())
        if (!backgroundManager!!.isAttached)
            backgroundManager!!.attach(getWindow())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initRows()
        initData()
    }

    private fun initUi() {
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        searchAffordanceColor = Color.DKGRAY
        setOnSearchClickedListener {
            toast("Soon, eh")
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

    private fun fetchTvs() {
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
        fetchTvs()
        onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, row ->
            mainRow = row.id.toInt()
            when (mainRow) {
                0 -> {
                    val position = movieAdapter?.indexOf(item)!!
                    if (position > moviesLoadOffset - 5) {
                        fetchMovies()
                    }
                    mainPosition = position
                }
                1 -> {
                    val position = tvShowAdapter?.indexOf(item)!!
                    if (position > tvsLoadOffset - 5) {
                        fetchTvs()
                    }
                    mainPosition = position
                }
            }
            if (item is Movie) {
                item.getCover()?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
            } else {
                backgroundManager?.color = ContextCompat.getColor(requireContext(), R.color.default_background2)
            }
        }
    }

    private fun initRows() {
        initAdapters()
        val movieHeader = HeaderItem(0, getString(R.string.menu_movies))
        val tvsHeader = HeaderItem(1, getString(R.string.menu_series))
        val preferenceHeader = HeaderItem(2, getString(R.string.settings_title))
        val favoriteHeader = HeaderItem(3, getString(R.string.menu_favorites))
        dbMovieViewModel.getMovies()?.observe(viewLifecycleOwner, Observer {
            for (favorite in it) {
                favoriteAdapter?.add(favorite)
            }
        })
        mainAdapter?.add(ListRow(movieHeader, movieAdapter))
        mainAdapter?.add(ListRow(tvsHeader, tvShowAdapter))
        mainAdapter?.add(ListRow(favoriteHeader, favoriteAdapter))
        mainAdapter?.add(ListRow(preferenceHeader, setupPreferenceAdapter()))
        adapter = mainAdapter
    }

    private fun initAdapters() {
        mainAdapter = ArrayObjectAdapter(ListRowPresenter())
        movieAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
        tvShowAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
        favoriteAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
    }

    private fun setupPreferenceAdapter(): ArrayObjectAdapter {
        val preferenceAdapter = ArrayObjectAdapter(PreferencePresenter(requireContext()))
        preferenceAdapter.add(PreferenceModel(getString(R.string.filter_change_lang), 1))
        preferenceAdapter.add(PreferenceModel(getString(R.string.filter_change_category), 2))
        preferenceAdapter.add(PreferenceModel(getString(R.string.filter_change_year), 3))
        preferenceAdapter.add(PreferenceModel(getString(R.string.filter_change_sort), 4))
        return preferenceAdapter
    }

    private fun handleMovies(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            for (model in dataList) {
                movieAdapter?.add(model)
            }
            if (moviesLoadOffset > 0) {
                val listRow = mainAdapter?.get(0) as ListRow
                val listRowAdapter = listRow.adapter as ArrayObjectAdapter
                listRowAdapter.notifyArrayItemRangeChanged(moviesLoadOffset, listRowAdapter.size())
            }
            moviesPage++
            moviesLoadOffset += 20
        }
    }

    private fun handleTvs(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            for (model in dataList) {
                tvShowAdapter?.add(model)
            }
            if (tvsLoadOffset > 0) {
                val listRow = mainAdapter?.get(1) as ListRow
                val listRowAdapter = listRow.adapter as ArrayObjectAdapter
                listRowAdapter.notifyArrayItemRangeChanged(moviesLoadOffset, listRowAdapter.size())
            }
            tvsPage++
            tvsLoadOffset += 20
        }
    }

}