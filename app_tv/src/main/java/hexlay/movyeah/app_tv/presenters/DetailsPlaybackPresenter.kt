package hexlay.movyeah.app_tv.presenters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import hexlay.movyeah.api.models.Movie

class DetailsPlaybackPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        val movie = item as Movie
        viewHolder.title.text = movie.getTitle()
    }

}