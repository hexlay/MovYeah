package hexlay.movyeah.fragments

import hexlay.movyeah.fragments.base.AbsMoviesFragment
import kotlinx.android.synthetic.main.fragment_movies.*

class MoviesFragment : AbsMoviesFragment() {

    override fun loadMovies() {
        movieListViewModel.fetchMainMovies(
                page = page,
                filtersLanguage = language,
                filtersGenres =  if (categories.size > 0) {
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
        )
    }

    override fun initFilter() {
        super.initFilter()
        fab_filter.setOnClickListener {
            filter = FilterFragment.newInstance(this@MoviesFragment)
            filter!!.show(childFragmentManager, filter!!.tag)
        }
    }

}
