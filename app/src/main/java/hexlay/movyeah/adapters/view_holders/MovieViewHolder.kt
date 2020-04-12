package hexlay.movyeah.adapters.view_holders

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import hexlay.movyeah.R
import hexlay.movyeah.activities.DetailActivity
import hexlay.movyeah.helpers.setUrl
import hexlay.movyeah.models.events.StartWatchingEvent
import hexlay.movyeah.models.movie.Movie
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.intentFor

class MovieViewHolder(itemView: View) : ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    var year: TextView = itemView.findViewById(R.id.year)
    var imdb: TextView = itemView.findViewById(R.id.imdb)
    var image: ImageView = itemView.findViewById(R.id.image)

    init {
        title.isSelected = true
        title.bringToFront()
        year.bringToFront()
        imdb.bringToFront()
    }

    fun bind(movie: Movie, activity: Activity) {
        title.isSelected = true
        title.text = movie.getTitle()
        year.text = movie.year.toString()
        imdb.text = movie.getRating("imdb").score.toString()
        movie.getTruePoster()?.let { image.setUrl(it) }
        itemView.setOnClickListener {
            EventBus.getDefault().post(StartWatchingEvent(movie))
        }
        itemView.setOnLongClickListener {
            activity.startActivity(activity.intentFor<DetailActivity>("movie" to movie))
            return@setOnLongClickListener false
        }
    }

}