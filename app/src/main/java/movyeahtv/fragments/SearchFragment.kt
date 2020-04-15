package movyeahtv.fragments

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import hexlay.movyeah.R
import hexlay.movyeah.api.view_models.MovieListViewModel
import hexlay.movyeah.helpers.getWindow
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.helpers.setDrawableFromUrl
import hexlay.movyeah.models.movie.Movie
import movyeahtv.presenters.MoviePresenter


class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private val movieListViewModel by viewModels<MovieListViewModel>()
    private var backgroundManager: BackgroundManager? = null

    private var searchText = ""
    private var page = 1
    private var superAdapter: ArrayObjectAdapter? = null
    private var searchAdapter: ArrayObjectAdapter? = null
    private val perPage = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        initBackgroundManager()
        initAdapters()
        initRows()
        backgroundManager?.color = ContextCompat.getColor(requireContext(), R.color.default_background)
        setOnItemViewSelectedListener { _, item, _, _ ->
            val position = searchAdapter?.indexOf(item)!!
            val page = ((position + 1) / perPage) + 1
            if (position > 0 && page == this.page) {
                fetchSearch()
            }
            if (item is Movie) {
                item.getCover()?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
            } else {
                backgroundManager?.color = ContextCompat.getColor(requireContext(), R.color.default_background)
            }
        }
    }

    private fun initAdapters() {
        val listRowPresenter = ListRowPresenter()
        listRowPresenter.setNumRows(1)
        superAdapter = ArrayObjectAdapter(listRowPresenter)
        searchAdapter = ArrayObjectAdapter(MoviePresenter(requireContext()))
    }

    private fun initRows() {
        val header = HeaderItem("ძიების შედეგები")
        superAdapter?.add(ListRow(header, searchAdapter))
        superAdapter?.notifyArrayItemRangeChanged(0, superAdapter!!.size())
    }

    private fun initBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(requireActivity())
        if (!backgroundManager!!.isAttached)
            backgroundManager!!.attach(getWindow())
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (query.isNotEmpty()) {
            searchText = query
            fetchSearch()
        }
        return false
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return superAdapter!!
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        return false
    }

    private fun fetchSearch() {
        movieListViewModel.fetchSearchMovie(page, searchText).observeOnce(viewLifecycleOwner, Observer {
            handleMovies(it)
        })
    }

    private fun handleMovies(dataList: List<Movie>) {
        if (dataList.isNotEmpty()) {
            searchAdapter?.addAll(searchAdapter!!.size(), dataList)
            page++
        }
    }

}