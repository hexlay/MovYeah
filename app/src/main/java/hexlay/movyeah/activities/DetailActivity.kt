package hexlay.movyeah.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import hexlay.movyeah.R
import hexlay.movyeah.adapters.view_holders.MovieViewHolder
import hexlay.movyeah.api.view_models.MovieListViewModel
import hexlay.movyeah.helpers.observeOnce
import hexlay.movyeah.helpers.setUrl
import hexlay.movyeah.helpers.toHtml
import hexlay.movyeah.helpers.toHumanDuration
import hexlay.movyeah.models.movie.Movie
import kotlinx.android.synthetic.main.activity_details.*

class DetailActivity : AppCompatActivity() {

    private val movieListViewModel by viewModels<MovieListViewModel>()
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initMovieData()
        initMovieView()
        initRelated()
    }

    private fun initMovieData() {
        if (intent.extras == null || intent.extras!!.isEmpty) {
            onBackPressed()
        }
        movie = intent.getParcelableExtra("movie") as Movie
    }

    private fun initMovieView() {
        if (movie == null) {
            onBackPressed()
        }
        val detailTitle = "${movie!!.getTitle()} <small><small><small>${movie!!.year}</small></small></small>".toHtml()
        val detailImdb = getString(R.string.news_imdb).format(movie!!.getRating("imdb"))
        val detailGenres = movie!!.getGenresString()
        val detailDuration = getString(R.string.news_duration).format(movie!!.duration.toHumanDuration())
        val detailWatchCount = getString(R.string.news_watch).format(movie!!.getWatchString())
        title_text.text = detailTitle
        title_imdb.text = detailImdb
        title_duration.text = detailDuration
        title_genres.text = if (detailGenres.isEmpty())
            getString(R.string.full_cats_not_found)
        else
            detailGenres
        title_watched.text = detailWatchCount
        description_text.text = movie!!.getDescription()
        movie!!.getCover()?.let { cover_image.setUrl(it) }
        title_text.isSelected = true
        title_genres.isSelected = true
        frame.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initRelated() {
        movieListViewModel.fetchRelated(movie!!.id).observeOnce(this, Observer {
            loading_movies.isGone = true
            if (it != null) {
                handleRelated(it)
            } else {
                related_movies_title.isGone = true
            }
        })
    }

    private fun handleRelated(data: List<Movie>) {
        related_movies_holder.setup {
            withDataSource(dataSourceOf(data))
            withLayoutManager(LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false))
            withItem<Movie, MovieViewHolder>(R.layout.list_items) {
                onBind(::MovieViewHolder) { _, item ->
                    this.bind(item, this@DetailActivity)
                }
            }
        }
    }

}
