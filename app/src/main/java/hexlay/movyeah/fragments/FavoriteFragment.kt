package hexlay.movyeah.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.database.view_models.DbMovieViewModel
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.helpers.*
import kotlinx.android.synthetic.main.fragment_movies.*

class FavoriteFragment : Fragment() {

    private val dbMovies by viewModels<DbMovieViewModel>()
    private val source = emptyDataSource()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initReloader()
        initObserver()
        loadMovies()
    }

    private fun initView() {
        val recyclerPaddingTop = getStatusBarHeight() + getActionBarSize() + dpOf(10)
        movies_holder.setPadding(0, recyclerPaddingTop, 0, getActionBarSize())
    }

    private fun initReloader() {
        movies_reloader.isEnabled = false
    }

    private fun loadMovies() {
        movies_holder.setup {
            withDataSource(source)
            withLayoutManager(GridLayoutManager(requireContext(), Constants.RECYCLER_GRID_COUNT))
            withItem<Movie, MovieViewHolder>(R.layout.list_items_extended) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, reqActivity())
                }
            }
        }
    }

    private fun initObserver() {
        dbMovies.getMovies()?.observe(viewLifecycleOwner, {
            source.clear()
            if (it.isNotEmpty()) {
                source.addAll(it)
                warning_holder.isVisible = false
            } else {
                warning_holder.text = getString(R.string.loading_favs_fail)
                warning_holder.isVisible = true
            }
        })
    }

}
