package hexlay.movyeah.fragments

import androidx.core.view.isGone
import androidx.core.view.isVisible
import hexlay.movyeah.R
import hexlay.movyeah.fragments.base.AbsMoviesFragment
import hexlay.movyeah.helpers.dpOf
import hexlay.movyeah.helpers.setMargins
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.piece_scroll_up.*

class SearchFragment : AbsMoviesFragment() {

    private var searchText = ""

    override fun initFragment() {
        initRecyclerView()
        initScrollUp()
        initFilter()
        initSearch()
        handleObserver()
    }

    override fun initScrollUp() {
        super.initScrollUp()
        scroll_up.setMargins(bottom = dpOf(20))
    }

    private fun initSearch() {
        warning_holder.isVisible = true
        warning_holder.text = getString(R.string.search_text)
        movies_reloader.isEnabled = false
        loading_movies.isGone = true
    }

    override fun initFilter() {
        fab_filter.hide()
    }

    override fun loadMovies() {
        movieListViewModel.fetchSearchMovie(page, searchText)
    }

    fun search(text: String) {
        if (text.isEmpty())
            return
        searchText = text
        zeroLoadMovies()
    }

    override fun handleObserver() {
        super.handleObserver()
        fab_filter.hide()
    }

}