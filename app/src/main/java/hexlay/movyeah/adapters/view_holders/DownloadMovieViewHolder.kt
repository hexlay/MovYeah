package hexlay.movyeah.adapters.view_holders

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import hexlay.movyeah.R

class DownloadMovieViewHolder(itemView: View) : ViewHolder(itemView) {

    var title: TextView = itemView.findViewById(R.id.title)
    var remove: Button = itemView.findViewById(R.id.remove_button)
    var download: Button = itemView.findViewById(R.id.download_button)
    var progress: ProgressBar = itemView.findViewById(R.id.download_progress)

}