package hexlay.movyeah.fragments

import androidx.lifecycle.Observer
import hexlay.movyeah.fragments.base.AbsMoviesFragment
import hexlay.movyeah.helpers.observeOnce
import kotlinx.android.synthetic.main.fragment_movies.*

class MoviesFragment : AbsMoviesFragment() {

    override fun loadMovies() {
        movieListViewModel.fetchMovies(
                page = page,
                filtersLanguage = language,
                filtersGenres =  if (categories.size > 0) {
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

    override fun initFilter() {
        super.initFilter()
        fab_filter.setOnClickListener {
            filter = FilterFragment.newInstance(this@MoviesFragment)
            filter!!.show(childFragmentManager, filter!!.tag)
        }
    }

}
