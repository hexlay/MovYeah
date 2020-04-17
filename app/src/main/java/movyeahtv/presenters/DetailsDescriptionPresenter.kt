package movyeahtv.presenters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import hexlay.movyeah.helpers.toHtml
import hexlay.movyeah.helpers.toHumanDuration
import hexlay.movyeah.models.movie.Movie

class DetailsDescriptionPresenter(private val genres: String) : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val imdbScore = movie.getRating("imdb").score
        viewHolder.title.text = movie.getTitle()
        val subtitle = "IMDB: ${imdbScore}, წელი: ${movie.year}, ხანგრძლივობა: ${movie.duration.toHumanDuration()}"
        viewHolder.subtitle.text = subtitle.toHtml()
        val body = "${genres}<br><br>${movie.getDescription()}"
        viewHolder.body.text = body.toHtml()
    }

}