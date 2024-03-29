package hexlay.movyeah.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.api.network.view_models.MainViewModel
import hexlay.movyeah.helpers.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()
    private val geoMoviesDataSource = emptyDataSource()
    private val topMoviesDataSource = emptyDataSource()
    private val topSeriesDataSource = emptyDataSource()
    private val geoSeriesDataSource = emptyDataSource()
    private val premiereDataSource = emptyDataSource()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun setupLayout() {
        val scrollerPaddingTop = getStatusBarHeight() + getActionBarSize()
        val scrollerPaddingBottom = getActionBarSize()
        scroller.setPadding(0, scrollerPaddingTop, 0, scrollerPaddingBottom)
    }

    private fun init() {
        setupLayout()
        setupRecyclerViews()
        val skeleton1 = geo_movies_holder.createSkeleton(R.layout.list_items).apply { showSkeleton() }
        mainViewModel.fetchGeoMovies().observeOnce(viewLifecycleOwner, {
            geoMoviesDataSource.addAll(it)
            skeleton1.showOriginal()
        })
        val skeleton2 = top_movies_holder.createSkeleton(R.layout.list_items).apply { showSkeleton() }
        mainViewModel.fetchTopMovies().observeOnce(viewLifecycleOwner, {
            topMoviesDataSource.addAll(it)
            skeleton2.showOriginal()
        })
        val skeleton3 = top_series_holder.createSkeleton(R.layout.list_items).apply { showSkeleton() }
        mainViewModel.fetchTopTvShows().observeOnce(viewLifecycleOwner, {
            topSeriesDataSource.addAll(it)
            skeleton3.showOriginal()
        })
        val skeleton4 = geo_series_holder.createSkeleton(R.layout.list_items).apply { showSkeleton() }
        mainViewModel.fetchGeoTvShows().observeOnce(viewLifecycleOwner, {
            geoSeriesDataSource.addAll(it)
            skeleton4.showOriginal()
        })
        val skeleton5 = premiere_movies_holder.createSkeleton(R.layout.list_items).apply { showSkeleton() }
        mainViewModel.fetchPremieres().observeOnce(viewLifecycleOwner, {
            premiereDataSource.addAll(it)
            skeleton5.showOriginal()
        })
    }

    private fun setupRecyclerViews() {
        geo_movies_holder.setup {
            withDataSource(geoMoviesDataSource)
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
        geo_movies_holder.setHasFixedSize(true)
        top_movies_holder.setup {
            withDataSource(topMoviesDataSource)
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
        top_movies_holder.setHasFixedSize(true)
        top_series_holder.setup {
            withDataSource(topSeriesDataSource)
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
        top_series_holder.setHasFixedSize(true)
        geo_series_holder.setup {
            withDataSource(geoSeriesDataSource)
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
        geo_series_holder.setHasFixedSize(true)
        premiere_movies_holder.setup {
            withDataSource(premiereDataSource)
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
        premiere_movies_holder.setHasFixedSize(true)
    }

}
