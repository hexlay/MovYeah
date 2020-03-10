package hexlay.movyeah.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hexlay.movyeah.R
import hexlay.movyeah.helpers.setUrl
import hexlay.movyeah.helpers.toCommaList
import hexlay.movyeah.helpers.toHtml
import hexlay.movyeah.helpers.toHumanDuration
import hexlay.movyeah.models.movie.Movie
import kotlinx.android.synthetic.main.activity_details.*

class DetailActivity : AppCompatActivity() {

    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        initMovieData()
        initMovieView()
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
        val detailImdb = getString(R.string.news_imdb).format(movie!!.getRating("imdb").score)
        val detailGenres = movie!!.genres?.data?.map { it.primaryName }?.toCommaList().toString()
        val detailDuration = getString(R.string.news_duration).format(movie!!.duration.toHumanDuration())
        val detailWatchCount = getString(R.string.news_watch).format(movie!!.watchCount)
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
}
