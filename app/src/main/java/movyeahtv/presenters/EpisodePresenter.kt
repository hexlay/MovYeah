package movyeahtv.presenters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import hexlay.movyeah.models.movie.attributes.show.Episode
import movyeahtv.models.events.watch.WatchEpisodeChangeEvent
import org.greenrobot.eventbus.EventBus


class EpisodePresenter(private val context: Context) : Presenter() {

    inner class ViewHolder(view: View) : Presenter.ViewHolder(view) {

        val episode = view as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = TextView(context)
        view.layoutParams = ViewGroup.LayoutParams(315, 175)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.setPadding(5, 5, 5, 5)
        view.setBackgroundColor(ContextCompat.getColor(context, hexlay.movyeah.R.color.default_background))
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val holder = viewHolder as ViewHolder
        val episode = item as Episode
        holder.episode.text = episode.getEpisodeTitle()
        holder.episode.setOnClickListener {
            EventBus.getDefault().post(WatchEpisodeChangeEvent(0, episode.episode - 1))
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}


}