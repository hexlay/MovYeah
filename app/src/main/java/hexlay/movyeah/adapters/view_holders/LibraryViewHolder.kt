package hexlay.movyeah.adapters.view_holders

import android.view.View
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import hexlay.movyeah.R

class LibraryViewHolder(itemView: View) : ViewHolder(itemView) {

    var resource: TextView = itemView.findViewById(R.id.resource_text)
    var summary: TextView = itemView.findViewById(R.id.summary_text)

}