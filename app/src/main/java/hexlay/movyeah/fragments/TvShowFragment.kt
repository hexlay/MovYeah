package hexlay.movyeah.fragments

import hexlay.movyeah.fragments.base.AbsMoviesFragment
import hexlay.movyeah.models.Filter
import org.greenrobot.eventbus.Subscribe

class TvShowFragment : AbsMoviesFragment() {

    override var filter: Filter = Filter(this)

    override fun loadMovies() {
        movieListViewModel.fetchMainMovies(
                page = page,
                filtersType = "series",
                filtersLanguage = filter.language,
                filtersGenres = if (filter.categories.size > 0) {
                    filter.categories.joinToString { it }
                } else {
                    null
                },
                filtersCountries =  if (filter.countries.size > 0) {
                    filter.countries.joinToString { it }
                } else {
                    null
                },
                filtersSort = filter.sortingMethod,
                filtersYears = "${filter.startYear},${filter.endYear}"
        )
    }

    @Subscribe
    fun listenFilterChange(filter: Filter) {
        if (filter.activeFragment is TvShowFragment) {
            if (this.filter != filter) {
                this.filter = filter
                zeroLoadMovies()
            }
        }
    }

}
