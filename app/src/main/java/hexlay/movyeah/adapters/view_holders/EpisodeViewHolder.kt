package hexlay.movyeah.adapters.view_holders

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.movyeah.R


class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.name)
    var languages: TextView = itemView.findViewById(R.id.languages)
    var base: RelativeLayout = itemView.findViewById(R.id.base)
    var download: ImageButton = itemView.findViewById(R.id.download_episode)

    init {
        title.isSelected = true
        title.bringToFront()
    }

    fun paintSelected() {
        base.setBackgroundColor(Color.DKGRAY)
        title.setTextColor(Color.WHITE)
        languages.setTextColor(Color.WHITE)
        download.setColorFilter(Color.WHITE)
    }

    fun paintDeSelected() {
        base.setBackgroundColor(Color.TRANSPARENT)
        title.setTextColor(Color.GRAY)
        languages.setTextColor(Color.GRAY)
        download.setColorFilter(Color.DKGRAY)
    }

}