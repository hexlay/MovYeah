package movyeahtv.presenters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import hexlay.movyeah.models.movie.Movie

class DetailsPlaybackPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        val movie = item as Movie
        viewHolder.title.text = movie.getTitle()
    }

}