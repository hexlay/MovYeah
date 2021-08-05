package hexlay.movyeah.adapters.view_holders

import android.app.ActivityOptions
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.card.MaterialCardView
import hexlay.movyeah.R
import hexlay.movyeah.activities.DetailActivity
import hexlay.movyeah.activities.MovieActivity
import hexlay.movyeah.api.models.Movie
import hexlay.movyeah.helpers.setUrl
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

class MovieViewHolder(itemView: View) : ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    private var year: TextView = itemView.findViewById(R.id.year)
    private var imdb: TextView = itemView.findViewById(R.id.imdb)
    private var image: ImageView = itemView.findViewById(R.id.image)
    private var container: MaterialCardView = itemView.findViewById(R.id.movie_container)

    init {
        title.isSelected = true
        title.bringToFront()
        year.bringToFront()
        imdb.bringToFront()
    }

    fun bind(movie: Movie, activity: FragmentActivity) {
        title.isSelected = true
        title.text = movie.getTitle()
        year.text = movie.year.toString()
        if (movie.getRating("imdb") == 0.0) {
            imdb.isVisible = false
        } else {
            imdb.isVisible = true
            imdb.text = movie.getRating("imdb").toString()
        }
        movie.getTruePoster()?.let { image.setUrl(it) }
        itemView.setOnClickListener {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, container, "movie_transition")
            activity.startActivity(activity.intentFor<MovieActivity>("movie" to movie).clearTop(), options.toBundle())
        }
        if (activity !is DetailActivity) {
            itemView.setOnLongClickListener {
                activity.startActivity(activity.intentFor<DetailActivity>("movie" to movie))
                return@setOnLongClickListener false
            }
        }
    }

}