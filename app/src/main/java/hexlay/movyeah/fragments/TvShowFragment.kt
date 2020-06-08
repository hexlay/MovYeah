package hexlay.movyeah.fragments

import androidx.lifecycle.Observer
import hexlay.movyeah.fragments.base.AbsMoviesFragment
import hexlay.movyeah.helpers.observeOnce
import kotlinx.android.synthetic.main.fragment_movies.*

class TvShowFragment : AbsMoviesFragment() {

    override fun loadMovies() {
        movieListViewModel.fetchMovies(
                page = page,
                filtersType = "series",
                filtersLanguage = language,
                filtersGenres = if (categories.size > 0) {
                    categories.joinToString { it }
                } else {
                    null
                },
                filtersCountries =  if (countries.size > 0) {
                    countries.joinToString { it }
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
            filter = FilterFragment.newInstance(this@TvShowFragment)
            filter!!.show(childFragmentManager, filter!!.tag)
        }
    }

}
