package movyeahtv.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.afollestad.inlineactivityresult.startActivityForResult
import hexlay.movyeah.R
import hexlay.movyeah.api.view_models.MovieListViewModel
import hexlay.movyeah.helpers.getWindow
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.movie.Movie
import movyeahtv.activities.TvWatchActivity
import movyeahtv.helpers.setDrawableFromUrl
import movyeahtv.models.events.StartActivityEvent
import movyeahtv.presenters.MoviePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class TvSearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private val movieListViewModel by viewModels<MovieListViewModel>()
    private var backgroundManager: BackgroundManager? = null

    private var searchText = ""
    private var page = 1
    private var superAdapter: ArrayObjectAdapter? = null
    private var searchAdapter: ArrayObjectAdapter? = null
    private val perPage = 20
    private var savedCover: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackgroundManager()
        initAdapters()
        initRows()
        initVoiceSearch()
        initItemListener()
    }

    private fun initVoiceSearch() {
        runWithPermissions(Permission.RECORD_AUDIO) {
            // TODO: Still idk DD
        }
    }

    @Subscribe
    fun listenActivityStart(event: StartActivityEvent) {
        when (event.key) {
            "TvWatchActivity" -> {
                startActivityForResult<TvWatchActivity>(event.params, requestCode = 3) { _, _ ->
                    savedCover?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
                }
            }
        }
    }

    private fun initItemListener() {
        setOnItemViewSelectedListener { _, item, _, _ ->
            val position = searchAdapter?.indexOf(item)!!
            val page = ((position + 1) / perPage) + 1
            if (position > 0 && page == this.page) {
                fetchSearch()
            }
            if (item is Movie) {
                savedCover = item.getCover()
                savedCover?.let { backgroundManager?.setDrawableFromUrl(requireContext(), it) }
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
        backgroundManager?.color = ContextCompat.getColor(requireContext(), R.color.default_background)
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

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

}