package hexlay.movyeah.app_tv.presenters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.app_tv.helpers.toHtml
import hexlay.movyeah.app_tv.helpers.toHumanDuration

class DetailsDescriptionPresenter(private val genres: String) : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val imdbScore = movie.getRating("imdb")
        viewHolder.title.text = movie.getTitle()
        val subtitle = "IMDB: ${imdbScore}, წელი: ${movie.year}, ხანგრძლივობა: ${movie.duration.toHumanDuration()}"
        viewHolder.subtitle.text = subtitle.toHtml()
        val body = "${genres}<br><br>${movie.getDescription()}"
        viewHolder.body.text = body.toHtml()
    }

}