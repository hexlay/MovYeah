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
import com.faltenreich.skeletonlayout.Skeleton
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.MovieListViewModel
import hexlay.movyeah.fragments.FilterFragment
import hexlay.movyeah.helpers.*
import hexlay.movyeah.models.Filter
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.piece_scroll_up.*

abstract class AbsMoviesFragment : Fragment() {

    protected val movieListViewModel by viewModels<MovieListViewModel>()
    protected var page = 1
    private var loading = true
    private var filterFragment: FilterFragment? = null

    private lateinit var skeleton: Skeleton

    protected abstract var filter: Filter

    private val source = emptyDataSourceTyped<Movie>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

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
        initReloader()
        initRecyclerView()
        initFilter()
        initScrollUp()
        zeroLoadMovies()
        handleObserver()
    }

    protected fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
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
        skeleton = movies_holder.createSkeleton(R.layout.list_items_extended, 10)
    }

    protected open fun initFilter() {
        fab_filter.setMargins(bottom = getActionBarSize() + dpOf(15))
        fab_filter.setOnClickListener {
            filterFragment = FilterFragment.newInstance(filter)
            filterFragment!!.show(childFragmentManager, filterFragment!!.tag)
        }
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
        fab_filter.hide()
        loadMovies()
        warning_holder.isVisible = false
        skeleton.showSkeleton()
    }

    protected open fun handleObserver() {
        movieListViewModel.movies.observe(requireActivity(), { dataList ->
            if (dataList != null) {
                if (dataList.isNotEmpty()) {
                    warning_holder.isGone = true
                    if (page > 1) {
                        source.addAll(dataList)
                        loading = true
                    } else {
                        source.clear()
                        source.addAll(dataList)
                        fab_filter.show()
                        skeleton.showOriginal()
                    }
                    page++
                } else {
                    if (page == 1) {
                        warning_holder.text = getString(R.string.loading_news_fail)
                        warning_holder.isVisible = true
                        source.clear()
                        fab_filter.show()
                        skeleton.showOriginal()
                    }
                }
            }
        })
    }

}