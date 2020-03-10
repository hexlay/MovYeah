package hexlay.movyeah.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.dataSourceOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.view_models.MainViewModel
import hexlay.movyeah.helpers.getActionBarSize
import hexlay.movyeah.helpers.getStatusBarHeight
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.models.movie.Movie
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()

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
        mainViewModel.fetchMovies()
        mainViewModel.geoMovies.observeOnce(viewLifecycleOwner, Observer {
            setupView(geo_movies_holder, it)
            loading_geo_movies.isGone = true
        })
        mainViewModel.topMovies.observeOnce(viewLifecycleOwner, Observer {
            setupView(top_movies_holder, it)
            loading_top_movies.isGone = true
        })
        mainViewModel.topTvShows.observeOnce(viewLifecycleOwner, Observer {
            setupView(top_series_holder, it)
            loading_top_series.isGone = true
        })
        mainViewModel.geoTvShows.observeOnce(viewLifecycleOwner, Observer {
            setupView(geo_series_holder, it)
            loading_geo_series.isGone = true
        })
        mainViewModel.premieres.observeOnce(viewLifecycleOwner, Observer {
            setupView(premiere_movies_holder, it)
            loading_premiere_movies.isGone = true
        })
    }

    private fun setupView(recyclerView: RecyclerView, data: List<Movie>) {
        recyclerView.setup {
            withDataSource(dataSourceOf(data))
            withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, requireActivity())
                }
            }
        }
    }

}
